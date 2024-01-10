package org.project.backend.jenkins;

import lombok.RequiredArgsConstructor;
import org.project.backend.credential.jenkins.JenkinsCredentials;
import org.project.backend.credential.jenkins.JenkinsCredentialsRepository;
import org.project.backend.rulefiles.RuleFile;
import org.project.backend.rulefiles.RuleFileRepository;
import org.project.backend.rulefiles.RuleFileService;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final UserRepository userRepository;

    private final RuleFileService ruleFileService;

    private final JenkinsCredentialsRepository credentialsRepository;

    public void create(String jobName, String path) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> user = userRepository.findByEmail(username);
            JenkinsCredentials jenkinsCredentials = credentialsRepository.findByUserId(user.get().getId());

            String jenkinsUsername = jenkinsCredentials.getUsername();
            String jenkinsToken = jenkinsCredentials.getAccessToken();

            System.out.println("Jenkins Username: " + jenkinsUsername);
            System.out.println("Jenkins Token: " + jenkinsToken);

            String jenkinsUrl = "http://localhost:8080/";

            try {
                String createJobUrl = jenkinsUrl + "createItem?name=" + URLEncoder.encode(jobName, "UTF-8");

                Path xmlPath = Path.of(path);
                String xmlContent = Files.readString(xmlPath);

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(createJobUrl))
                        .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsUsername + ":" + jenkinsToken).getBytes()))
                        .header("Content-Type", "application/xml")
                        .POST(HttpRequest.BodyPublishers.ofString(xmlContent))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                if (statusCode == 200) {
                    System.out.println("Job created successfully!");
                } else {
                    System.out.println("Failed to create job. Status code: " + statusCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void execute(String jobName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> user = userRepository.findByEmail(username);
            JenkinsCredentials jenkinsCredentials = credentialsRepository.findByUserId(user.get().getId());

            String jenkinsUsername = jenkinsCredentials.getUsername();
            String jenkinsToken = jenkinsCredentials.getAccessToken();

            System.out.println("Jenkins Username: " + jenkinsUsername);
            System.out.println("Jenkins Token: " + jenkinsToken);
            String jenkinsUrl = "http://localhost:8080/";
            try {
                String jobUrl = jenkinsUrl + "job/" + jobName + "/build";

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(jobUrl))
                        .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsUsername + ":" + jenkinsToken).getBytes()))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                if (statusCode == 201) {
                    System.out.println("Job triggered successfully!");
                } else {
                    System.out.println("Failed to trigger job. Status code: " + statusCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Função que atualiza o conteúdo do ficheiro .xml do jenkins, modificando o nome do ficheiro de regras do cpilint,
     * nome do iflow e nome do ficheiro de regras do CodeNarc.
     *
     * @param patternString
     * @param filePath
     * @param placeString
     */
    public static void updateJenkinsFile(String patternString, String filePath, String placeString) {
        try {
            // Lê o conteúdo do arquivo
            Path path = Paths.get(filePath);
            String originalPipeline = new String(Files.readAllBytes(path));

            // Define um padrão regex para encontrar a seção específica
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(originalPipeline);

            // Substitui os caminhos originais pelos novos caminhos
            String updatedPipeline = matcher.replaceFirst(placeString);

            // Escreve o conteúdo atualizado de volta no arquivo
            Files.write(path, updatedPipeline.getBytes(), StandardOpenOption.WRITE);

            System.out.println("Pipeline atualizado e gravado com sucesso no arquivo: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao processar o arquivo do pipeline.");
        }
    }

    public void executeUpdateJenkinsFile(String jenkinsXmlPath, String ruleFileName) {
        String relativePath = "src/main/java/org/project/backend/jenkins/temp";
        Path projectPath = Paths.get(System.getProperty("user.dir"));
        Path path = projectPath.resolve(relativePath);
        String ruleFilePath = path.toString();


        // Get the rule file content from the database
        Optional<RuleFile> optionalRuleFile = Optional.ofNullable(ruleFileService.getRuleFileByName(ruleFileName));
        if (optionalRuleFile.isPresent()) {
            RuleFile ruleFile = optionalRuleFile.get();

            // Create a temporary file with the rule file content
            try {
                ruleFilePath = ruleFilePath + "/" + ruleFile.getFileName();
                Files.write(Path.of(ruleFilePath), ruleFile.getFileContent());
                System.out.println("Temporary file created with content from the database: " + ruleFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating temporary file with content from the database.");
            }

            // CPI Lint

            String patternString_cpi = "sh '/cp/cpilint-1.0.4/bin/cpilint -rules /files/(.*?) -files /files/(.*?)'";
            String placeString_cpi = "sh '/cp/cpilint-1.0.4/bin/cpilint -rules /files/novo -files /files/novo'";
            updateJenkinsFile(patternString_cpi, jenkinsXmlPath, placeString_cpi);


            // Codenarc
            String patternString_codenarc1 = "sh 'unzip /files/firstflow.zip -d /files/(.*?)'";
            String placeString_codenarc1 = "sh 'unzip /files/firstflow.zip -d /files/novo'";
            updateJenkinsFile(patternString_codenarc1, jenkinsXmlPath, placeString_codenarc1);

            String patternString_codenarc2 = "sh 'java -cp /cp/codenarc.jar org.codenarc.CodeNarc -rulesetfiles=file:/files/(.*?) -basedir=/files/unzip_flow/(.*?)'";
            String placeString_codenarc2 = "sh 'java -cp /cp/codenarc.jar org.codenarc.CodeNarc -rulesetfiles=file:/files/novo -basedir=/files/unzip_flow/novo'";
            updateJenkinsFile(patternString_codenarc2, jenkinsXmlPath, placeString_codenarc2);
        } else {
            System.out.println("RuleFile not found in the database.");
        }
    }
}

package org.project.backend.jenkins;

import lombok.RequiredArgsConstructor;
import org.project.backend.configuration_files.codenarc.CodenarcFile;
import org.project.backend.configuration_files.codenarc.CodenarcFileService;
import org.project.backend.credential.jenkins.JenkinsCredentials;
import org.project.backend.credential.jenkins.JenkinsCredentialsRepository;
import org.project.backend.jenkins.deserializer.CPIlintDeserializer;
import org.project.backend.jenkins.deserializer.CodenarcReportReaderDeserializer;
import org.project.backend.jenkins.deserializer.DependencyCheckReportReaderDeserializer;
import org.project.backend.jenkins.dto.ReportDTO;
import org.project.backend.repository.github.GithubRepositoryService;
import org.project.backend.configuration_files.cpi.RuleFile;
import org.project.backend.configuration_files.cpi.RuleFileService;
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

    private final CodenarcFileService codenarcFileService;

    private final JenkinsCredentialsRepository credentialsRepository;

    private final GithubRepositoryService githubRepositoryService;
    private final CodenarcReportReaderDeserializer codenarcReportReaderDeserializer;
    private final DependencyCheckReportReaderDeserializer dependencyCheckReportReaderDeserializer;
    private final CPIlintDeserializer cpIlintDeserializer;

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

    public void executeUpdateJenkinsFile(String jenkinsXmlPath, String ruleFileName, String codenarcFileName, String githubFileName) throws IOException, InterruptedException {
        String relativePath = "src/main/java/org/project/backend/jenkins/temp";
        Path projectPath = Paths.get(System.getProperty("user.dir"));
        Path path = projectPath.resolve(relativePath);
        String savePath = path.toString();
        savePath += "/";
        String ruleFilePath = path.toString();
        String codenarcFilePath = path.toString();

        githubFileName += ".zip";

        // Get the rule file content from the database
        Optional<RuleFile> optionalRuleFile = Optional.ofNullable(ruleFileService.getRuleFileByName(ruleFileName));

        // Get the codenarc file content from the database
        Optional<CodenarcFile> optionalCodenarcFile = Optional.ofNullable(codenarcFileService.getCodenarcFileByName(codenarcFileName));

        if (optionalRuleFile.isPresent() && optionalCodenarcFile.isPresent()) {
            RuleFile ruleFile = optionalRuleFile.get();
            CodenarcFile codenarcFile = optionalCodenarcFile.get();

            // Create a temporary file with the rule file content
            try {
                ruleFilePath = ruleFilePath + "/" + ruleFile.getFileName();
                Files.write(Path.of(ruleFilePath), ruleFile.getFileContent());
                System.out.println("Temporary file created with content from the database: " + ruleFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating temporary file with content from the database.");
            }

            // Create a temporary file with the codenarc file content
            try {
                codenarcFilePath = codenarcFilePath + "/" + codenarcFile.getFileName();
                Files.write(Path.of(codenarcFilePath), codenarcFile.getFileContent());
                System.out.println("Created Codenarc Temporary file " + codenarcFile.getFileName() + " created with content from the database in " + codenarcFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating temporary file with content from the database.");
            }

            String patternString_flow = "def FlowZip = '/files/(.*?)'";
            String patternString_CPI_Rules = "def CPILintRules = '/files/(.*?)'";
            String patternString_Codenarc_Rules = "def CodenarcRules = '/files/(.*?)'";

            String placeString_flow = "def FlowZip = '/files/" + githubFileName + "'";
            String placeString_CPI_Rules = "def CPILintRules = '/files/" + ruleFileName + "'";
            String placeString_Codenarc_Rules = "def CodenarcRules = '/files/" + codenarcFileName + "'";

            updateJenkinsFile(patternString_flow,jenkinsXmlPath, placeString_flow);

            updateJenkinsFile(patternString_CPI_Rules,jenkinsXmlPath, placeString_CPI_Rules);

            updateJenkinsFile(patternString_Codenarc_Rules,jenkinsXmlPath, placeString_Codenarc_Rules);
        } else {
            System.out.println("RuleFile not found in the database.");
        }


        // Get the IFlow zip from Github
        githubRepositoryService.downloadFileFromGitHub(githubFileName,savePath);
    }

    public ReportDTO getJenkinsReport(){
        //TODO: pass the name of the file as a parameter
        //TODO: call this method the right method in jenkins when pipeline as finished
       return ReportDTO.builder()
                .codenarcReport(codenarcReportReaderDeserializer.deserialize())
                .dependencyCheckReport(dependencyCheckReportReaderDeserializer.deserialize())
                .cpiLintReport(cpIlintDeserializer.deserialize())
                .build();
    }
}

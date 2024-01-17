package org.project.backend.jenkins;

import org.project.backend.packages.FlowResponseDTO;
import org.project.backend.packages.PackagesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final UserRepository userRepository;

    private final RuleFileService ruleFileService;

    private final CodenarcFileService codenarcFileService;

    private final PackagesService packagesService;

    private final JenkinsCredentialsRepository credentialsRepository;

    private final GithubRepositoryService githubRepositoryService;
    private final CodenarcReportReaderDeserializer codenarcReportReaderDeserializer;
    private final DependencyCheckReportReaderDeserializer dependencyCheckReportReaderDeserializer;
    private final CPIlintDeserializer cpIlintDeserializer;

    @Value("${path.external}")
    private String externalPath;

    @Value("${path.internal}")
    private String internalPath;

    public void create(String jobName) {
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

            Path projectPath = Paths.get(externalPath);
            String path = projectPath + "\\file.xml";

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


    //TODO - Adicionar REPORT AQUI
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

    public static void updateJenkinsFile(String patternString, Resource resource, String placeString) {
        try {
            // Lê o conteúdo do arquivo
            Path path = Paths.get(resource.getURI());
            String originalPipeline = new String(Files.readAllBytes(path));

            // Define um padrão regex para encontrar a seção específica
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(originalPipeline);

            // Substitui os caminhos originais pelos novos caminhos
            String updatedPipeline = matcher.replaceFirst(placeString);

            // Escreve o conteúdo atualizado de volta no arquivo
            Files.write(path, updatedPipeline.getBytes(), StandardOpenOption.WRITE);

            System.out.println("Pipeline atualizado e gravado com sucesso no arquivo.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao processar o arquivo do pipeline.");
        }
    }

    public void executeUpdateJenkinsFile(Resource resource, String ruleFileName, String codenarcFileName, String githubFileName, String flowVersion) throws IOException, InterruptedException {
        Path projectPath = Paths.get(externalPath);
        String ruleFilePath = projectPath.toString();
        String codenarcFilePath = projectPath.toString();
        String githubFilePath = projectPath.toString();

        githubFileName += ".zip";

        // Full path to the destination file in WSL
        String wslDestinationPath = projectPath + "\\file.xml";

        try (InputStream inputStream = resource.getInputStream()) {
            Path destination = Path.of(wslDestinationPath);

            // Ensure that the destination directories exist, create them if necessary
            Files.createDirectories(destination.getParent());

            // Use Files.copy to copy the content of the InputStream to the destination
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File moved successfully to WSL folder.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to move the file to WSL folder: " + e.getMessage());
        }

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

            updateJenkinsFile(patternString_flow,resource, placeString_flow);

            updateJenkinsFile(patternString_CPI_Rules,resource, placeString_CPI_Rules);

            updateJenkinsFile(patternString_Codenarc_Rules,resource, placeString_Codenarc_Rules);
        } else {
            System.out.println("RuleFile not found in the database.");
        }


        // Get the IFlow zip from CPI API
        ResponseEntity<byte[]> response = packagesService.downloadFlow(githubFileName, flowVersion);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            // Create a temporary file with the github file content
            try {
                githubFilePath = githubFilePath + "/" + githubFileName;      //    _data/my_integration_flow.zip
                Path filePath = Path.of(githubFilePath);
                Files.write(filePath, response.getBody());
                System.out.println("Temporary file created with content from the database: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating or writing to the temporary file.");
            }
        } else {
            System.out.println("Failed to retrieve IFlow zip from CPI API.");
        }


        // Get the IFlow zip from Github
        //githubRepositoryService.downloadFileFromGitHub(githubFileName,savePath);
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

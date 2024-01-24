package org.project.backend.jenkins;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.project.backend.configuration_files.codenarc.CodenarcFile;
import org.project.backend.configuration_files.codenarc.CodenarcFileService;
import org.project.backend.configuration_files.cpi.RuleFile;
import org.project.backend.configuration_files.cpi.RuleFileService;
import org.project.backend.credential.jenkins.JenkinsCredentials;
import org.project.backend.credential.jenkins.JenkinsCredentialsRepository;
import org.project.backend.exception.BadRequestException;
import org.project.backend.jenkins.deserializer.CPIlintDeserializer;
import org.project.backend.jenkins.deserializer.CodenarcReportReaderDeserializer;
import org.project.backend.jenkins.deserializer.DependencyCheckReportReaderDeserializer;
import org.project.backend.jenkins.dto.ReportDTO;
import org.project.backend.packages.PackagesService;
import org.project.backend.repository.github.GithubRepositoryService;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.project.backend.exception.enumeration.ExceptionType.*;

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
    private final ResourceLoader resourceLoader;

    @Value("${path.external}")
    private String externalPath;

    @Value("${path.internal}")
    private String internalPath;

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    public void create(String jobName) {
        var jenkinsCredentials = getJenkinsCredentials();
        String jenkinsUsername = jenkinsCredentials.getUsername();
        String jenkinsToken = jenkinsCredentials.getAccessToken();

        Path projectPath = Paths.get(externalPath);
        String path = projectPath + "/" + "file.xml";

        try {
            String createJobUrl = jenkinsUrl + "createItem?name=" + URLEncoder.encode(jobName, StandardCharsets.UTF_8);

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
                System.out.println(response.body());
                System.out.println("Failed to create job. Status code: " + statusCode);
                throw new BadRequestException("Failed to create job", FAILED_TO_CREATE_JOB);
            }
        } catch (IOException | InterruptedException e) {
            throw new BadRequestException("Failed to create job", FAILED_TO_CREATE_JOB);
        }
    }

    public ReportDTO execute(String jobName) {
        var jenkinsCredentials = getJenkinsCredentials();
        String jenkinsUsername = jenkinsCredentials.getUsername();
        String jenkinsToken = jenkinsCredentials.getAccessToken();

        System.out.println("Jenkins Username: " + jenkinsUsername);
        System.out.println("Jenkins Token: " + jenkinsToken);
        try {
            String jobUrl = jenkinsUrl + "job/" + jobName + "/build";
            System.out.println(java.util.Base64.getEncoder().encodeToString((jenkinsUsername + ":" + jenkinsToken).getBytes()));
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

                var state = checkBuildState(jobName);
                if (state.equals("SUCCESS") || state.equals("FAILURE")) {
                    System.out.println("Job executed successfully!");
                    sendFlowZipToGithub(jobName);
                    sendReportToGithub(jobName);
                    return getJenkinsReport(jobName);
                } else {
                    System.out.println("Job executed with errors!");
                    throw new BadRequestException("Job executed with errors", JOB_FINISHED_WITH_FAILURE);
                }

            } else {
                System.out.println("Failed to trigger job. Status code: " + statusCode);
                throw new BadRequestException("Failed to trigger job", FAILED_TO_EXECUTE_JOB);
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to trigger job", FAILED_TO_EXECUTE_JOB);
        }
    }


    public void sendFlowZipToGithub (String fileName) throws IOException, InterruptedException {
        String uploadDir = externalPath;
        fileName += ".zip";
        Path filePath = Path.of(uploadDir, fileName);
        byte[] content = Files.readAllBytes(filePath);
        githubRepositoryService.sendFileToGitHub("main", fileName, content);
    }

    public void sendReportToGithub(String fileName) throws IOException, InterruptedException {

        String uploadDir = internalPath + "workspace" + "/" + fileName;
        System.out.println("Upload dir: " + uploadDir);
        File flowDirectory = new File(uploadDir);
        if (!flowDirectory.exists()) {
          throw new BadRequestException("Directory not found", DIRECTORY_NOT_FOUND);
        }

        File cpilintLog = new File(uploadDir + "/" + "cpilint.log");
        File dependencyCheckLog = new File(uploadDir + "/" + "dependency-check-report.json");
        File codenarcLog = new File(uploadDir + "/" + "output.json");

        byte[] contentCpi = Files.readAllBytes(cpilintLog.toPath());
        byte[] contentDependencyCheck = Files.readAllBytes(dependencyCheckLog.toPath());
        byte[] contentCodenarc = Files.readAllBytes(codenarcLog.toPath());
        githubRepositoryService.sendFileToGitHub("main", "cpilint.log", contentCpi);
        githubRepositoryService.sendFileToGitHub("main", "dependency-check-report.json", contentDependencyCheck);
        githubRepositoryService.sendFileToGitHub("main", "output.json", contentCodenarc);
    }

    public void executeUpdateJenkinsFile(String ruleFileName, String codenarcFileName, String flowFileName, String flowVersion) {
        Path projectPath = Paths.get(externalPath);
        String githubFilePath = projectPath.toString();
        var pipelineFilePath = movePipelineFileToJenkins();

        // Get the rule file content from the database
        Optional<RuleFile> optionalRuleFile = Optional.ofNullable(ruleFileService.getRuleFileByName(ruleFileName));

        // Get the codenarc file content from the database
        Optional<CodenarcFile> optionalCodenarcFile = Optional.ofNullable(codenarcFileService.getCodenarcFileByName(codenarcFileName));

        if (optionalRuleFile.isPresent() && optionalCodenarcFile.isPresent()) {
            RuleFile ruleFile = optionalRuleFile.get();
            CodenarcFile codenarcFile = optionalCodenarcFile.get();

            sendFileToJenkins(ruleFile.getFileContent(), ruleFile.getFileName());
            sendFileToJenkins(codenarcFile.getFileContent(), codenarcFile.getFileName());

            String patternString_flow = "def FlowZip = '/files/(.*?)'";
            String patternString_CPI_Rules = "def CPILintRules = '/files/(.*?)'";
            String patternString_Codenarc_Rules = "def CodenarcRules = '/files/(.*?)'";

            String placeString_flow = "def FlowZip = '/files/" + flowFileName + ".zip'";
            String placeString_CPI_Rules = "def CPILintRules = '/files/" + ruleFileName + "'";
            String placeString_Codenarc_Rules = "def CodenarcRules = '/files/" + codenarcFileName + "'";
            System.out.println(pipelineFilePath);
            updateJenkinsFile(patternString_flow, pipelineFilePath, placeString_flow);

            updateJenkinsFile(patternString_CPI_Rules, pipelineFilePath, placeString_CPI_Rules);

            updateJenkinsFile(patternString_Codenarc_Rules, pipelineFilePath, placeString_Codenarc_Rules);
        } else {
            System.out.println("RuleFile not found in the database.");
            throw new BadRequestException("RuleFile not found in the database.", RULE_FILE_NOT_FOUND);
        }
    }

    public String checkBuildState(String jobName) {
        var jenkinsCredentials = getJenkinsCredentials();
        String jenkinsUsername = jenkinsCredentials.getUsername();
        String jenkinsToken = jenkinsCredentials.getAccessToken();
        boolean building = false;
        String result = null;
        do {
            try {
                TimeUnit.SECONDS.sleep(10);
                String JobStateUrl = jenkinsUrl + "job/" + jobName + "/lastBuild/api/json";

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(JobStateUrl))
                        .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsUsername + ":" + jenkinsToken).getBytes()))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                if (statusCode == 200) {
                    JSONObject jsonResponse = new JSONObject(response.body());

                    building = jsonResponse.getBoolean("building");
                    result = jsonResponse.optString("result", null);
                    System.out.println("Building: " + building + " Result:" + result);

                } else {
                    System.out.println("Failed to check job status. Status code: " + statusCode);
                    throw new BadRequestException("Failed to check job status", FAILED_TO_CHECK_JOB_STATUS);
                }
            } catch (InterruptedException | IOException e) {
                throw new BadRequestException("Failed to check job status", FAILED_TO_CHECK_JOB_STATUS);
            }
        } while (building);
        return result;
    }

    public ReportDTO getJenkinsReport(String jobName) {
        return ReportDTO.builder()
                .codenarcReport(codenarcReportReaderDeserializer.deserialize(jobName))
                .dependencyCheckReport(dependencyCheckReportReaderDeserializer.deserialize(jobName))
                .cpiLintReport(cpIlintDeserializer.deserialize(jobName))
                .build();
    }

    public void deletePipeline(String jobName) {
        var jenkinsCredentials = getJenkinsCredentials();
        try {
            String jobDeleteUrl = jenkinsUrl + "job/" + jobName + "/doDelete";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jobDeleteUrl))
                    .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsCredentials.getUsername() + ":" + jenkinsCredentials.getAccessToken()).getBytes()))
                    .header("Content-Type", "charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            // Handle redirect (HTTP status code 302)
            if (statusCode == 302) {
                // Get the new location from the 'Location' header
                String redirectUrl = response.headers().firstValue("Location").orElse(null);

                if (redirectUrl != null) {
                    System.out.println("Redirecting to: " + redirectUrl);

                    // Send another request to the new location
                    HttpRequest redirectRequest = HttpRequest.newBuilder()
                            .uri(URI.create(redirectUrl))
                            .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsCredentials.getUsername() + ":" + jenkinsCredentials.getAccessToken()).getBytes()))
                            .header("Content-Type", "charset=UTF-8")
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();

                    response = client.send(redirectRequest, HttpResponse.BodyHandlers.ofString());
                    statusCode = response.statusCode();
                }
            }

            if (statusCode == 200) {
                System.out.println("Job deleted successfully!");
            } else if (statusCode == 404) {
                System.out.println("Job not found!");
            } else {
                System.out.println("Failed to delete job. Status code: " + statusCode);
                throw new BadRequestException("Failed to delete job", FAILED_TO_DELETE_JOB);
            }
        } catch (IOException | InterruptedException e) {
            throw new BadRequestException("Failed to delete job", FAILED_TO_DELETE_JOB);
        }
    }

    public static void updateJenkinsFile(String patternString, Path path, String placeString) {
        try {
            // Lê o conteúdo do arquivo
            String originalPipeline = new String(Files.readAllBytes(path));

            // Define um padrão regex para encontrar a seção específica
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(originalPipeline);

            // Substitui os caminhos originais pelos novos caminhos
            String updatedPipeline = matcher.replaceFirst(placeString);

            // Escreve o conteúdo atualizado de volta no arquivo
            Files.write(path, updatedPipeline.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Pipeline atualizado e gravado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao processar o ficheiro do pipeline.");
            throw new BadRequestException("Erro ao processar o ficheiro do pipeline.", FAILED_TO_UPLOAD_FILE);
        }
    }

    private JenkinsCredentials getJenkinsCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> user = userRepository.findByEmail(username);
            return credentialsRepository.findByUserId(user.get().getId());
        }
        throw new BadRequestException("User not authenticated", USER_NOT_FOUND);
    }
    private Path movePipelineFileToJenkins() {
        Resource resource = resourceLoader.getResource("classpath:jenkins/file.xml");
        String fullDestinationPath = externalPath + "/" + "file.xml";
        Path destinationPath = Paths.get(fullDestinationPath);

        try (InputStream inputStream = resource.getInputStream()) {
            Files.createDirectories(destinationPath.getParent()); // if the directory does not exist, create it
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Resource file moved successfully to external Jenkins folder.");
        } catch (IOException e) {
            System.err.println("Failed to move the resource file to external Jenkins folder: " + e.getMessage());
            throw new BadRequestException("Failed to move the resource file to external Jenkins folder", FAILED_TO_MOVE_FILE);
        }
        return destinationPath;
    }

    private void sendFileToJenkins(byte[] fileContent, String fileName) {
        try {
            String filePath = externalPath + "/" + fileName;
            Files.write(Path.of(filePath), fileContent);
            System.out.println("File created with content: " + filePath);
        } catch (IOException e) {
            System.out.println("Error creating temporary file.");
            throw new BadRequestException("Files with rules not found", RULE_FILE_NOT_FOUND); // Rethrow the exception to indicate the failure
        }
    }

    public ResponseEntity<String> uploadFlowZip(MultipartFile zipFile) {
        if (zipFile.isEmpty()) {
            return new ResponseEntity<>("No file provided", HttpStatus.BAD_REQUEST);
        }

        try {
            // Specify the directory where you want to save the uploaded file
            String uploadDir = externalPath;

            // Ensure the directory exists, create it if not
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // Save the file to the specified directory
            String fileName = zipFile.getOriginalFilename();
            Path filePath = Path.of(uploadDir, fileName);
            Files.copy(zipFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

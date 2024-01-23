package org.project.backend.repository.github;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.project.backend.credential.github.GithubCredentials;
import org.project.backend.credential.github.GithubCredentialsRepository;
import org.project.backend.credential.github.GithubCredentialsService;
import org.project.backend.exception.ResourceNotFoundException;
import org.project.backend.repository.github.dto.GithubRepositoryEditRequest;
import org.project.backend.repository.github.dto.GithubRepositoryRegisterRequest;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GithubRepositoryService {
    private final UserRepository userRepository;
    private final GithubRepositoryRepository githubRepositoryRepository;
    private final GithubCredentialsService githubCredentialsService;
    private final GithubCredentialsRepository credentialsRepository;

    @Transactional
    public GithubRepository save(GithubRepositoryRegisterRequest githubRepositoryRegisterRequest) {
        var githubCredentials = githubRepositoryRegisterRequest.getGithubCredentials();


        System.out.println("Repository name: " + githubRepositoryRegisterRequest.getName());
        System.out.println("Repository main branch: " + githubRepositoryRegisterRequest.getMainBranch());
        System.out.println("Repository secondary branches: " + githubRepositoryRegisterRequest.getSecondaryBranches());
        System.out.println("Repository credentials: " + githubRepositoryRegisterRequest.getGithubCredentials());

        var githubRepository = GithubRepository.builder()
                .name(githubRepositoryRegisterRequest.getName())
                .mainBranch(githubRepositoryRegisterRequest.getMainBranch())
                .secondaryBranches(githubRepositoryRegisterRequest.getSecondaryBranches())
                .credentials(githubCredentials)
                .build();

        return githubRepositoryRepository.save(githubRepository);
    }

    @Transactional
    public GithubRepository update(GithubRepositoryEditRequest githubRepositoryEditRequest) {
        var githubRepository = githubRepositoryRepository.findById(githubRepositoryEditRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("GithubRepository", "id", githubRepositoryEditRequest.getId()));

        githubRepository.setName(githubRepositoryEditRequest.getName());
        githubRepository.setMainBranch(githubRepositoryEditRequest.getMainBranch());
        githubRepository.setSecondaryBranches(githubRepositoryEditRequest.getSecondaryBranches());

        return githubRepositoryRepository.save(githubRepository);
    }

    @Transactional
    public void delete(Long id) {
        githubRepositoryRepository.deleteById(id);
    }

    @Transactional
    public GithubRepository findByCredentials() {
        GithubCredentials credentials = githubCredentialsService.findByUser();
        var opt = githubRepositoryRepository.findByCredentials(credentials);
        return opt.orElse(null);
    }
    private static boolean branchExists(String branchName, String apiUrl, String token) throws IOException, InterruptedException {
        String url = apiUrl + "/git/refs/heads/" + branchName;
        HttpResponse<String> response = sendGetRequest(url, token);
        return response.statusCode() == 200;
    }

    private static void createBranch(String branchName, String apiUrl, String token) throws IOException, InterruptedException {
        // Obter o SHA da branch padrão (por exemplo, "main")
        String defaultBranch = getDefaultBranchSha(apiUrl, token);
        String defaultBranchSha = getLatestCommitSha(defaultBranch, apiUrl, token);
        System.out.println("default branch sha " + defaultBranchSha);
        // Criar a nova branch com base na branch padrão
        String url = apiUrl + "/git/refs";
        String json = String.format("{\"ref\":\"refs/heads/%s\",\"sha\":\"%s\"}", branchName, defaultBranchSha);

        HttpResponse<String> response = sendPostRequest(url,json, token);

        if (response.statusCode() != 201) {
            System.out.println("Erro ao criar a branch: " + response.body());
        }
        else {
            System.out.println("The branch " + branchName + " was created");
        }
    }

    private static String getDefaultBranchSha(String apiUrl, String token) throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest(apiUrl, token);
        Pattern pattern = Pattern.compile("\"default_branch\":\"(\\w+)\"");
        Matcher matcher = pattern.matcher(response.body());

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
    private static boolean isRepoValid(String apiUrl, String token) throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest(apiUrl, token);
        boolean isValid = response.statusCode() == 200;
        if (!isValid) System.out.println("The repo is not valid");
        return isValid;

    }

    private static boolean isTokenValid(String apiUrl, String token) throws IOException, InterruptedException {
        String url = apiUrl;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        boolean isValid = response.statusCode() == 200;
        if (!isValid) System.out.println("The Token is not valid");

        return isValid;
    }

    private static String getLatestCommitSha(String branch, String apiUrl, String token) throws IOException, InterruptedException {
        String url = apiUrl + "/git/refs/heads/" + branch;
        String responseBody = sendGetRequest(url, token).body();
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONObject object = (JSONObject) jsonResponse.get("object");
        return object.getString("sha");

    }

    private static String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            System.out.println("O caminho não é válido ou o arquivo não existe.");
        }
        return Base64.getEncoder().encodeToString(Files.readAllBytes(path));
    }

    private static String createBlob(String fileContent, String apiUrl, String token) throws IOException, InterruptedException {
        String url = apiUrl + "/git/blobs";
        String json = "{\"content\":\"" + fileContent + "\",\"encoding\":\"base64\"}";
        HttpResponse<String> response = sendPostRequest(url, json, token);
        String responseBody = response.body();

        JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse.getString("sha");

    }

    private static String createTree(String baseTreeSha, String filePath, String blobSha, String apiUrl, String token)
            throws IOException, InterruptedException {
        String url = apiUrl + "/git/trees";

        String encodedFilePath = URLEncoder.encode(filePath, StandardCharsets.UTF_8);
        String json = String.format("{\"base_tree\":\"%s\",\"tree\":[{\"path\":\"%s\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"%s\"}]}", baseTreeSha, encodedFilePath, blobSha);
        HttpResponse<String> response = sendPostRequest(url, json, token);
        String responseBody = response.body();

        JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse.getString("sha");
    }



    private static String createCommit(String treeSha, String apiUrl, String token) throws IOException, InterruptedException {
        String url = apiUrl + "/git/commits";
        String json = "{\"message\":\"File commit\",\"tree\":\"" + treeSha + "\"}";
        HttpResponse<String> response = sendPostRequest(url, json, token);
        String responseBody = response.body();

        JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse.getString("sha");
    }

    private static void updateBranchReference(String branch, String commitSha, String apiUrl, String token)
            throws IOException, InterruptedException {
        String url = apiUrl + "/git/refs/heads/" + branch;
        String json = "{\"sha\":\"" + commitSha + "\",\"force\":true}";
        HttpResponse<String> response = sendPatchRequest(url, json, token);
        System.out.println(response);
    }

    private static HttpResponse.BodyHandler<String> responseBodyHandler() {
        return HttpResponse.BodyHandlers.ofString();
    }

    private static HttpResponse<String> sendGetRequest(String url, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .build();

        return HttpClient.newHttpClient().send(request, responseBodyHandler());
    }

    private static HttpResponse<String> sendPostRequest(String url, String json, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return HttpClient.newHttpClient().send(request, responseBodyHandler());
    }

    private static HttpResponse<String> sendPatchRequest(String url, String json, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();

        return HttpClient.newHttpClient().send(request, responseBodyHandler());
    }

    public void sendZipToGitHub(String branch, String filePath, byte[] zipContentBytes) throws IOException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Obter o user
            Optional<User> user = userRepository.findByEmail(username);

            // Obter as credenciais
            GithubCredentials githubCredentials = credentialsRepository.findByUserId(user.get().getId());
            String githubToken = githubCredentials.getAccessToken();
            String githubUsername = githubCredentials.getUsername();

            // Obter o repositorio associado às credenciais de github
            Optional<GithubRepository> githubRepository = githubRepositoryRepository.findByCredentials(githubCredentials);
            String repo = githubRepository.get().getName();

            String githubApiUrl = "https://api.github.com/repos/" + githubUsername + "/" + repo;

            if (isTokenValid(githubApiUrl, githubToken) && isRepoValid(githubApiUrl, githubToken)) {

                // 0. Verificar se a branch existe, se não existir, criar uma nova
                if (!branchExists(branch, githubApiUrl, githubToken)) {
                    System.out.println("Branch doesn't exist, creating a new one...");
                    createBranch(branch, githubApiUrl, githubToken);
                }

                // 1. Obter SHA da última confirmação na branch desejada
                String commitSha = getLatestCommitSha(branch, githubApiUrl, githubToken);
                System.out.println("last commit SHA: " + commitSha);

                // 1.1 Read ZIP file content
                String zipContent = Base64.getEncoder().encodeToString(zipContentBytes);

                // 2. Criar um novo blob no repositório
                String blobSha = createBlob(zipContent, githubApiUrl, githubToken);
                System.out.println("SHA of Blob: " + blobSha);

                // 3. Criar uma nova árvore contendo o blob
                String newTreeSha = createTree(commitSha, filePath, blobSha, githubApiUrl, githubToken);
                System.out.println("SHA of Tree: " + newTreeSha);

                // 4. Criar um novo commit apontando para a nova árvore
                String newCommitSha = createCommit(newTreeSha, githubApiUrl, githubToken);
                System.out.println("SHA of new Commit: " + newCommitSha);

                // 5. Atualizar a referência da branch para o novo commit
                updateBranchReference(branch, newCommitSha, githubApiUrl, githubToken);

                System.out.println("Success on push!");
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    public List<String> getAllBranches() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> branches = new ArrayList<>();
        String mainBranch = "";
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Obter o user
            Optional<User> user = userRepository.findByEmail(username);

            // Obter as credenciais
            GithubCredentials githubCredentials = credentialsRepository.findByUserId(user.get().getId());

            // Obter o repositorio associado às credenciais de github
            Optional<GithubRepository> githubRepository = githubRepositoryRepository.findByCredentials(githubCredentials);
            branches = githubRepository.get().getSecondaryBranches();
            mainBranch = githubRepository.get().getMainBranch();
            branches.add(0, mainBranch);

            // print every branch
            for (String branch : branches) {
                System.out.println(branch);
            }
        }
        return branches;
    }

    public void downloadFileFromGitHub(String fileName, String savePath) throws IOException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        savePath += fileName;

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Obtain the user
            Optional<User> user = userRepository.findByEmail(username);

            // Obtain the credentials
            GithubCredentials githubCredentials = credentialsRepository.findByUserId(user.get().getId());
            String githubToken = githubCredentials.getAccessToken();
            String githubUsername = githubCredentials.getUsername();

            // Obtain the repository associated with the GitHub credentials
            Optional<GithubRepository> githubRepository = githubRepositoryRepository.findByCredentials(githubCredentials);
            String repo = githubRepository.get().getName();

            // https://api.github.com/repos/{username}/{repository_name}/contents/{file_path}
            String githubApiUrl = "https://api.github.com/repos/" + githubUsername + "/" + repo;

            if (isTokenValid(githubApiUrl, githubToken) && isRepoValid(githubApiUrl, githubToken)) {
                // Construct the URL for getting the file content from the repository
                System.out.println("VALID TOKEN AND REPONAME");
                String fileContentUrl = githubApiUrl + "/contents/" + fileName;

                // Create an HttpRequest
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(fileContentUrl))
                        .header("Authorization", "Bearer " + githubToken)
                        .GET()
                        .build();

                // Create an HttpClient
                HttpClient httpClient = HttpClient.newHttpClient();

                // Send the request and handle the response
                try {
                    HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

                    if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                        // Save the file locally
                        Path targetPath = Path.of(savePath);
                        Files.copy(response.body(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File downloaded successfully to: " + savePath);
                    } else {
                        System.out.println("Failed to download file. HTTP Status Code: " + response.statusCode());
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

}
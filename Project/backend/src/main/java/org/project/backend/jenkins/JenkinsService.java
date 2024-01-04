package org.project.backend.jenkins;

import lombok.RequiredArgsConstructor;
import org.project.backend.credential.github.GithubCredentials;
import org.project.backend.credential.github.GithubCredentialsRepository;
import org.project.backend.credential.jenkins.JenkinsCredentials;
import org.project.backend.credential.jenkins.JenkinsCredentialsRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final UserRepository userRepository;

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
}

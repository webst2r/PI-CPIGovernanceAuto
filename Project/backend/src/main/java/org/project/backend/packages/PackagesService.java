package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.project.backend.credential.Credential;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.project.backend.credential.CredentialRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackagesService {

    @Value("${external.api.baseurl}")
    private String externalApiBaseUrl;

    private final WebClient.Builder webClientBuilder;

    private final UserRepository userRepository;

    private final CredentialRepository credentialsRepository;


    public PackagesResponseDTO obtainSecuredResource() throws JsonProcessingException {
        // Obtain the current authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve the user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Now you have the username, you can use it to retrieve the user from the repository
            Optional<User> user = userRepository.findByEmail(username);

            // Rest of your code remains the same...
            Credential credentials = credentialsRepository.findByUserId(user.get().getId());

            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            log.debug("clientId: {}", clientId);
            log.debug("clientSecret: {}", clientSecret);

            String accessTokenValue = obtainAccessToken(clientId, clientSecret).block();
            log.debug("Access token: {}", accessTokenValue);

            String jsonResponse = webClientBuilder.build()
                    .get()
                    .uri("https://45438691trial.it-cpitrial05.cfapps.us10-001.hana.ondemand.com/api/v1/IntegrationPackages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class).block();

            // Deserialize JSON into IntegrationPackage object
            ObjectMapper objectMapper = new ObjectMapper();
            PackagesResponseDTO packagesResponse = objectMapper.readValue(jsonResponse, PackagesResponseDTO.class);

            return packagesResponse;
        } else {
            // Handle the case where the user is not authenticated
            throw new RuntimeException("User not authenticated");
        }
    }


    private Mono<String> obtainAccessToken(String clientId, String clientSecret) {
        return webClientBuilder.build()
                .post()
                .uri("https://45438691trial.authentication.us10.hana.ondemand.com/oauth/token")
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .onStatus(status -> status.isError(), response -> response.toEntity(String.class)
                        .flatMap(entity -> {
                            log.error("Error obtaining access token. Status code: {}, Response: {}", response.statusCode(), entity.getBody());
                            return Mono.error(new RuntimeException("Error obtaining access token"));
                        }))
                .bodyToMono(JsonNode.class)
                .map(tokenResponse -> tokenResponse.get("access_token").textValue())
                .onErrorResume(throwable -> {
                    log.error("Error obtaining access token", throwable);
                    return Mono.error(new RuntimeException("Error obtaining access token", throwable));
                });
    }

    public PackageDetailsResponseDTO getPackageDetails(String packageId) throws JsonProcessingException {
        // Obtain the current authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve the user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Now you have the username, you can use it to retrieve the user from the repository
            Optional<User> user = userRepository.findByEmail(username);

            // Rest of your code remains the same...
            Credential credentials = credentialsRepository.findByUserId(user.get().getId());

            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            log.debug("clientId: {}", clientId);
            log.debug("clientSecret: {}", clientSecret);

            String accessTokenValue = obtainAccessToken(clientId, clientSecret).block();
            log.debug("Access token: {}", accessTokenValue);

            // Use the packageId parameter in the URI for the specific package details
            String packageDetailsUri = externalApiBaseUrl + "/api/v1/IntegrationPackages" + "('" + packageId + "')" + "/IntegrationDesigntimeArtifacts";

            String jsonResponse = webClientBuilder.build()
                    .get()
                    .uri(packageDetailsUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class).block();

            // Deserialize JSON into PackageDetailsResponseDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            PackageDetailsResponseDTO packageDetailsResponse = objectMapper.readValue(jsonResponse, PackageDetailsResponseDTO.class);

            return packageDetailsResponse;
        } else {
            // Handle the case where the user is not authenticated
            throw new RuntimeException("User not authenticated");
        }
    }


}

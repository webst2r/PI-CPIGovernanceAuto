package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.backend.credential.sap_cpi.CredentialSapCpi;
import org.project.backend.credential.sap_cpi.CredentialSapCpiRepository;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final WebClient.Builder webClientBuilder;

    private final UserRepository userRepository;

    private final CredentialSapCpiRepository credentialsRepository;


    public PackagesResponseDTO getPackages() throws JsonProcessingException {
        // Obtain the current authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve the user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // retrieve the user from the repository
            Optional<User> user = userRepository.findByEmail(username);
            CredentialSapCpi credentials = credentialsRepository.findByUserId(user.get().getId());

            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            String uri = credentials.getBaseUrl() + "/api/v1/IntegrationPackages";
            String tokenUrl = credentials.getTokenUrl();
            log.debug("clientId: {}", clientId);
            log.debug("clientSecret: {}", clientSecret);
            log.debug("base uri: {}", uri);
            log.debug("tokenUrl: {}", tokenUrl);

            String accessTokenValue = obtainAccessToken(clientId, clientSecret, tokenUrl).block();
            log.debug("Access token: {}", accessTokenValue);

            String jsonResponse = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class).block();

            // Deserialize JSON into IntegrationPackage object
            ObjectMapper objectMapper = new ObjectMapper();
            PackagesResponseDTO packagesResponse = objectMapper.readValue(jsonResponse, PackagesResponseDTO.class);

            return packagesResponse;
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    public IntegrationPackage getPackage(String packageId) throws JsonProcessingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Optional<User> user = userRepository.findByEmail(username);

            CredentialSapCpi credentials = credentialsRepository.findByUserId(user.get().getId());
            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            String uri = credentials.getBaseUrl() + "/api/v1/IntegrationPackages" + "('" + packageId + "')";
            String tokenUrl = credentials.getTokenUrl();
            log.debug("clientId: {}", clientId);
            log.debug("clientSecret: {}", clientSecret);
            log.debug("base uri: {}", uri);
            log.debug("tokenUrl: {}", tokenUrl);

            String accessTokenValue = obtainAccessToken(clientId, clientSecret, tokenUrl).block();
            log.debug("Access token: {}", accessTokenValue);

            String jsonResponse = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class).block();


            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(IntegrationPackage.class, new PackageResponseDTODeserializer());
            objectMapper.registerModule(module);

            IntegrationPackage packageResponse = objectMapper.readValue(jsonResponse, IntegrationPackage.class);

            return packageResponse;
        } else {
            // Handle the case where the user is not authenticated
            throw new RuntimeException("User not authenticated");
        }
    }


    private Mono<String> obtainAccessToken(String clientId, String clientSecret, String tokenUri) {

        return webClientBuilder.build()
                .post()
                .uri(tokenUri)
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

    public PackageFlowsResponseDTO getPackageFlows(String packageId) throws JsonProcessingException {
        // Obtain the current authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve the user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> user = userRepository.findByEmail(username);

            CredentialSapCpi credentials = credentialsRepository.findByUserId(user.get().getId());

            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            String packageDetailsUri = credentials.getBaseUrl() + "/api/v1/IntegrationPackages" + "('" + packageId + "')" + "/IntegrationDesigntimeArtifacts";
            String tokenUrl = credentials.getTokenUrl();
            log.debug("clientId: {}", clientId);
            log.debug("clientSecret: {}", clientSecret);
            log.debug("base uri: {}", packageDetailsUri);
            log.debug("tokenUrl: {}", tokenUrl);

            String accessTokenValue = obtainAccessToken(clientId, clientSecret, tokenUrl).block();
            log.debug("Access token: {}", accessTokenValue);

            String jsonResponse = webClientBuilder.build()
                    .get()
                    .uri(packageDetailsUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class).block();

            // Deserialize JSON into PackageDetailsResponseDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            PackageFlowsResponseDTO packageDetailsResponse = objectMapper.readValue(jsonResponse, PackageFlowsResponseDTO.class);

            return packageDetailsResponse;
        } else {
            // Handle the case where the user is not authenticated
            throw new RuntimeException("User not authenticated");
        }
    }


    public FlowResponseDTO getFlow(String flowId, String flowVersion) throws JsonProcessingException {
        // Obtain the current authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve the user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> user = userRepository.findByEmail(username);

            CredentialSapCpi credentials = credentialsRepository.findByUserId(user.get().getId());

            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            String flowDetailsUri = credentials.getBaseUrl() + "/api/v1/IntegrationDesigntimeArtifacts" + "(Id=" + "'" + flowId + "'" + ",Version=" + "'" + flowVersion + "'"+ ")";
            String tokenUrl = credentials.getTokenUrl();
            log.debug("clientId: {}", clientId);
            log.debug("clientSecret: {}", clientSecret);
            log.debug("base uri: {}", flowDetailsUri);
            log.debug("tokenUrl: {}", tokenUrl);

            String accessTokenValue = obtainAccessToken(clientId, clientSecret, tokenUrl).block();
            log.debug("Access token: {}", accessTokenValue);

            String jsonResponse = webClientBuilder.build()
                    .get()
                    .uri(flowDetailsUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class).block();

            // Deserialize JSON into PackageDetailsResponseDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            FlowResponseDTO flowDetailsResponse = objectMapper.readValue(jsonResponse, FlowResponseDTO.class);

            return flowDetailsResponse;
        } else {
            // Handle the case where the user is not authenticated
            throw new RuntimeException("User not authenticated");
        }
    }

    public ResponseEntity<byte[]> downloadFlow(String flowId, String flowVersion) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Optional<User> user = userRepository.findByEmail(username);
            CredentialSapCpi credentials = credentialsRepository.findByUserId(user.get().getId());
            String clientId = credentials.getClientId();
            String clientSecret = credentials.getClientSecret();
            String flowDownloadUri = credentials.getBaseUrl() + "/api/v1/IntegrationDesigntimeArtifacts" +
                    "(Id=" + "'" + flowId + "'" + ",Version=" + "'" + flowVersion + "'" + ")" + "/$value?";
            String tokenUrl = credentials.getTokenUrl();

            // Obtain the access token
            String accessTokenValue = obtainAccessToken(clientId, clientSecret, tokenUrl).block();

            // Make the request to the external API
            ResponseEntity<byte[]> responseEntity = webClientBuilder.build()
                    .get()
                    .uri(flowDownloadUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .exchange()
                    .block()
                    .toEntity(byte[].class)
                    .block();

            HttpHeaders headers = new HttpHeaders();

            String contentDisposition = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
            if (contentDisposition != null) {
                String fileName = extractFileName(contentDisposition);
                headers.setContentDispositionFormData("attachment", fileName);
            } else {
                headers.setContentDispositionFormData("attachment", "default_filename.xml");
            }

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return new ResponseEntity<>(responseEntity.getBody(), headers, HttpStatus.OK);
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    private String extractFileName(String contentDisposition) {
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            int startIndex = contentDisposition.indexOf("filename=") + 9;
            int endIndex = contentDisposition.indexOf(";", startIndex);

            if (endIndex == -1) {
                endIndex = contentDisposition.length();
            }

            String fileName = contentDisposition.substring(startIndex, endIndex);

            // Remove surrounding quotes, if present
            fileName = fileName.replaceAll("^\"|\"$", "");

            return fileName;
        }

        return "defaultFileName"; // Provide a default if filename is not found
    }


}

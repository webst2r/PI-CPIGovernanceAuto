package org.project.backend.flows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;

import org.project.backend.credential.Credential;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.project.backend.credential.CredentialRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.logging.Logger;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlowsService {

    @Value("${external.api.baseurl}")
    private String externalApiBaseUrl;

    private final WebClient.Builder webClientBuilder;

    private final UserRepository userRepository;

    private final CredentialRepository credentialsRepository;

    //private static final Logger LOG = (Logger) LoggerFactory.getLogger(FlowsService.class);

    public PackagesResponseDTO obtainSecuredResource(Integer userId) throws JsonProcessingException {
        User user = userRepository.findById(userId).orElseThrow();

        Credential credentials = credentialsRepository.findByUserId(user.getId());
        String clientId = credentials.getClientId();
        String clientSecret = credentials.getClientSecret();

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


    public FlowsResponse getPackages() {
        return new FlowsResponse("all good");


    }
}

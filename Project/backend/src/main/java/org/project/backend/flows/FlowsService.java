package org.project.backend.flows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class FlowsService {

    @Value("${external.api.baseurl}")
    private String externalApiBaseUrl;

    private final WebClient webClient;

    public FlowsService(WebClient webClient) {
        this.webClient = webClient;
    }

    public FlowsResponse getPackages() {

         /*
         String externalApiUrl = externalApiBaseUrl + "/api/v1/IntegrationPackages";

        // Use WebClient to make a request to an external API
        String apiResponse = webClient.get()
                .uri(externalApiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

         */
        return new FlowsResponse("all good");


    }
}

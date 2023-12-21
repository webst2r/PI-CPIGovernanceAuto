// FlowsService.java
package org.project.backend.flows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlowsService {

    @Value("${external.api.baseurl}")
    private String externalApiBaseUrl;

    public String getPackageFlows(String packageName) {
        String externalApiUrl = externalApiBaseUrl + "/api/v1/IntegrationPackages('" + packageName + "')/IntegrationDesigntimeArtifacts";

        // Fazer o GET à API do CPI
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(externalApiUrl, String.class);

        return response;
    }
}

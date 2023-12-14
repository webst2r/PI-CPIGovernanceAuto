import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

// DOWNLOAD DE INTEGRATION FLOWS COMO FICHEIRO ZIP
@RestController
public class IntegrationFlowController {
    @GetMapping("/IntegrationDesigntimeArtifacts")
    public ResponseEntity<byte[]> downloadIntegrationFlow(
            @RequestParam String Id,
            @RequestParam String Version,
            HttpServletResponse response) {

        // Substitua os seguintes valores pelos detalhes reais da sua API do SAP CPI
        String sapCpiBaseUrl = "https://sandbox.api.sap.com/cpi/api/v1";
        String integrationEndpoint = String.format("/IntegrationDesigntimeArtifacts(Id=%s, Version=%s)/$value", Id, Version);
        String sapCpiUrl = sapCpiBaseUrl + integrationEndpoint;

        // Use RestTemplate para fazer a chamada GET
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> integrationFlowResponse = restTemplate.getForEntity(sapCpiUrl, byte[].class);

        // Verifique se a chamada foi bem-sucedida
        if (integrationFlowResponse.getStatusCode().is2xxSuccessful()) {
            // Configure o cabeçalho da resposta para fazer o download do arquivo
            response.setHeader("Content-Disposition", "attachment; filename=IntegrationFlow.zip");
            return integrationFlowResponse;
        } else {
            // Se não for bem-sucedido, você pode retornar uma mensagem de erro ou lidar de outra maneira
            return ResponseEntity.status(integrationFlowResponse.getStatusCode()).body(null);
        }
    }
}

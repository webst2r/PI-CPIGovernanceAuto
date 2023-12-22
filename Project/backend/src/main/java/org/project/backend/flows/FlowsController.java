package org.project.backend.flows;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flows")
public class FlowsController {

    private final FlowsService flowsService;

    @Autowired
    public FlowsController(FlowsService flowsService) {
        this.flowsService = flowsService;
    }


    @GetMapping("/getPackages/{id}")
    public ResponseEntity<PackagesResponseDTO> getPackages(@PathVariable("id") Integer id) throws JsonProcessingException {
        PackagesResponseDTO response = flowsService.obtainSecuredResource(id);
        return ResponseEntity.ok(response);
    }
}

package org.project.backend.flows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/flows")
public class FlowsController {

    private final FlowsService flowsService;

    @Autowired
    public FlowsController(FlowsService flowsService) {
        this.flowsService = flowsService;
    }

    @GetMapping("/getPackages")
    public ResponseEntity<FlowsResponse> getPackages() {
        FlowsResponse response = flowsService.getPackages();
        return ResponseEntity.ok(response);
    }
}

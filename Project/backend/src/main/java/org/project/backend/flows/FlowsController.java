package org.project.backend.flows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flows")
public class FlowsController {

    private final FlowsService flowsService;

    @Autowired
    public FlowsController(FlowsService flowsService) {
        this.flowsService = flowsService;
    }

    @GetMapping("/{packageName}/getPackageFlows")
    public ResponseEntity<String> getPackageFlows(@PathVariable String packageName) {
        String response = flowsService.getPackageFlows(packageName);
        return ResponseEntity.ok(response);
    }
}

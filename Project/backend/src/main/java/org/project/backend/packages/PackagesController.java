package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/packages")
public class PackagesController {

    private final PackagesService packagesService;

    @Autowired
    public PackagesController(PackagesService packagesService) {
        this.packagesService = packagesService;
    }


    @GetMapping("/getPackages")
    public ResponseEntity<PackagesResponseDTO> getPackages() throws JsonProcessingException {
        PackagesResponseDTO response = packagesService.getPackages();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getPackage/{id}")
    public ResponseEntity<IntegrationPackage> getPackage(@PathVariable("id") String packageId) throws JsonProcessingException {
        IntegrationPackage response = packagesService.getPackage(packageId);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/getPackageFlows/{id}")
    public ResponseEntity<PackageFlowsResponseDTO> getPackageFlows(@PathVariable("id") String packageId) throws JsonProcessingException {
        PackageFlowsResponseDTO response = packagesService.getPackageFlows(packageId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFlow/{id}/{version}")
    public ResponseEntity<FlowResponseDTO> getFlow(
            @PathVariable("id") String flowId,
            @PathVariable("version") String flowVersion) throws JsonProcessingException {
        FlowResponseDTO response = packagesService.getFlow(flowId, flowVersion);
        return ResponseEntity.ok(response);
    }




}

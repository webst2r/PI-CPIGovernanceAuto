package org.project.backend.packages;

import lombok.extern.slf4j.Slf4j;
import org.project.backend.github.GithubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
@RequestMapping("/api/packages")
public class PackagesController {

    private final PackagesService packagesService;

    private final GithubService gitHubService;

    @Autowired
    public PackagesController(PackagesService packagesService, GithubService gitHubService) {
        this.packagesService = packagesService;
        this.gitHubService = gitHubService;
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

    @GetMapping("/downloadFlow/{id}/{version}")
    public ResponseEntity<byte[]> downloadFlow(@PathVariable("id") String flowId, @PathVariable("version") String flowVersion) throws IOException, InterruptedException {
        // Fazer download do Flow para as transferências
        ResponseEntity<byte[]> response = packagesService.downloadFlow(flowId, flowVersion);


        // Enviar o Flow para o GitHub

        String branch = "teste";
        String filePath = "teste4.txt";

        try {
            gitHubService.sendFlowToGitHub(branch, filePath);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(response.getBody());
    }








}

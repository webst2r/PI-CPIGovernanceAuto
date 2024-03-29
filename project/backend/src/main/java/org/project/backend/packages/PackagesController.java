package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.backend.jenkins.JenkinsService;
import org.project.backend.jenkins.dto.ReportDTO;
import org.project.backend.repository.github.GithubRepositoryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/packages")
public class PackagesController {

    private final PackagesService packagesService;

    private final GithubRepositoryService gitHubService;

    private final JenkinsService jenkinsService;

    @GetMapping("/createAndExecutePipeline/{jobName}/{ruleFileName}/{codenarcFileName}/{flowVersion}")
    public ResponseEntity<ReportDTO> enableJenkins(
            @PathVariable("jobName") String jobName,
            @PathVariable("ruleFileName") String ruleFileName,
            @PathVariable("codenarcFileName") String codenarcFileName,
            @PathVariable("flowVersion") String flowVersion)
    {

            // Delete Jenkins job
            jenkinsService.deletePipeline(jobName);

            // Update jenkins xml file
            jenkinsService.executeUpdateJenkinsFile(ruleFileName, codenarcFileName, jobName, flowVersion);

            //Create Jenkins job
            jenkinsService.create(jobName);

            // Execute Jenkins job
            var report = jenkinsService.execute(jobName);
            //var report = new ReportDTO();
            return ResponseEntity.ok(report);
    }

    @GetMapping("/jenkinsReport")
    public ResponseEntity<ReportDTO> getJenkinsReport() {
        return ResponseEntity.ok(jenkinsService.getJenkinsReport("my_integration_flow_pi"));
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
    public ResponseEntity<byte[]> downloadFlow(@PathVariable("id") String flowId, @PathVariable("version") String flowVersion) {
        ResponseEntity<byte[]> response = packagesService.downloadFlow(flowId, flowVersion);
        return ResponseEntity.ok(response.getBody());
    }


    @PostMapping("/uploadFlowZip")
    public ResponseEntity<String> uploadFlowZip(@RequestParam("zipFile") MultipartFile zipFile) {
        jenkinsService.uploadFlowZip(zipFile);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/enableGithub/{id}/{version}/{branch}")
    public ResponseEntity<byte[]> enableGithub(
            @PathVariable("id") String flowId,
            @PathVariable("version") String flowVersion,
            @PathVariable("branch") String branch
    ) {

        // Fazer o download do Flow
        ResponseEntity<byte[]> response = packagesService.downloadFlow(flowId, flowVersion);

        // Enviar o Flow para o GitHub
        HttpHeaders headers = response.getHeaders();
        String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        String fileName = extractFileName(contentDisposition);

        try {
            byte[] zipContentBytes = convertBytesToZip(response.getBody(), fileName);
            String zipFileName = fileName.replace(".xml", ".zip");
            System.out.println("Zip Filename: " + zipFileName);
            gitHubService.sendFileToGitHub(branch, zipFileName, zipContentBytes);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(response.getBody());
    }

    private byte[] convertBytesToZip(byte[] xmlContentBytes, String fileName) throws IOException {
        try (ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipStream);
             ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlContentBytes)) {

            ZipEntry zipEntry = new ZipEntry(fileName.replace(".xml", ".zip"));
            zipOutputStream.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = xmlStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, length);
            }

            zipOutputStream.closeEntry();
            zipOutputStream.finish();

            return zipStream.toByteArray();
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

package org.project.backend.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.backend.jenkins.dto.codenarc.CodenarcReportDTO;
import org.project.backend.jenkins.dto.codenarc.FileDTO;
import org.project.backend.jenkins.dto.codenarc.PackageDTO;
import org.project.backend.jenkins.dto.codenarc.ViolationDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CodenarcReportReaderDeserializer {
    private final ResourceLoader resourceLoader;

    public CodenarcReportDTO deserialize() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Load the JSON file from the resources directory
            Resource resource = resourceLoader.getResource("classpath:" +"jenkins/codenarc.json");

            // Read JSON file and parse it into a CodeNarcReportDTO object
            CodenarcReportDTO codenarcReportDTO = objectMapper.readValue(resource.getInputStream(), CodenarcReportDTO.class);

            // Get the packages with violations
            List<PackageDTO> packagesWithViolations = getPackageDTOsWithViolations(codenarcReportDTO.getPackages());

           codenarcReportDTO.setPackages(packagesWithViolations);

            // Print the CodeNarcReportDTO object
            printCodenarcReportDTO(codenarcReportDTO);
            return codenarcReportDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

        private void printCodenarcReportDTO(CodenarcReportDTO codenarcReportDTO) {
        System.out.println("CodeNarc Packages:");

        // Print package information
        for (PackageDTO packageDTO : codenarcReportDTO.getPackages()) {
            System.out.println("Package Path: " + packageDTO.getPath());
            System.out.println("Total Files: " + packageDTO.getTotalFiles());
            System.out.println("Files With Violations: " + packageDTO.getFilesWithViolations());
            System.out.println("Priority 1 Violations: " + packageDTO.getPriority1());
            System.out.println("Priority 2 Violations: " + packageDTO.getPriority2());
            System.out.println("Priority 3 Violations: " + packageDTO.getPriority3());

            // Print file information
            System.out.println("Files in Package:");
            for (FileDTO fileDTO : packageDTO.getFiles()) {
                System.out.println("  File Name: " + fileDTO.getName());
                System.out.println("  Total Violations: " + fileDTO.getViolations().size());

                // Print violation information
                System.out.println("  Violations:");
                for (ViolationDTO violationDTO : fileDTO.getViolations()) {
                    System.out.println("    Rule: " + violationDTO.getRuleName());
                    System.out.println("    Priority: " + violationDTO.getPriority());
                    System.out.println("    Line Number: " + violationDTO.getLineNumber());
                    System.out.println("    Source Line: " + violationDTO.getSourceLine());
                    System.out.println("    Message: " + violationDTO.getMessage());
                    System.out.println();
                }
            }
            System.out.println();
        }

        // Print summary information
        System.out.println("Summary:");
        System.out.println("Total Files: " + codenarcReportDTO.getSummary().getTotalFiles());
        System.out.println("Files With Violations: " + codenarcReportDTO.getSummary().getFilesWithViolations());
        System.out.println("Priority 1 Violations: " + codenarcReportDTO.getSummary().getPriority1());
        System.out.println("Priority 2 Violations: " + codenarcReportDTO.getSummary().getPriority2());
        System.out.println("Priority 3 Violations: " + codenarcReportDTO.getSummary().getPriority3());
    }

    private List<PackageDTO> getPackageDTOsWithViolations(List<PackageDTO> allPackages) {
        // Filter packages with violations
       var packages = allPackages.stream()
               .filter(packageDTO -> !packageDTO.getFiles().isEmpty())
               .toList();

       packages.forEach(packageDTO -> {
           // Filter files with violations
           var files = packageDTO.getFiles().stream()
                   .filter(fileDTO -> !fileDTO.getViolations().isEmpty())
                   .toList();
           packageDTO.setFiles(files);
       });
       return packages;
    }
}

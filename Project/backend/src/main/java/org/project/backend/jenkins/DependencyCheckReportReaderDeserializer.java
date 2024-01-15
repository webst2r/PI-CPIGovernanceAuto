package org.project.backend.jenkins;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.backend.jenkins.dto.codenarc.CodenarcReportDTO;
import org.project.backend.jenkins.dto.codenarc.PackageDTO;
import org.project.backend.jenkins.dto.dependecycheck.DependencyCheckReportDTO;
import org.project.backend.jenkins.dto.dependecycheck.DependencyDTO;
import org.project.backend.jenkins.dto.dependecycheck.VulnerabilitiesDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DependencyCheckReportReaderDeserializer {
    private final ResourceLoader resourceLoader;

    public DependencyCheckReportDTO deserialize() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //TODO: pass the name of the file as a parameter and change the resource directory to the correct one
            Resource resource = resourceLoader.getResource("classpath:" +"jenkins/dependency-check-report.json");

            // Read JSON file and parse it into a CodeNarcReportDTO object
            DependencyCheckReportDTO dependencyCheckReportDTO = objectMapper.readValue(resource.getInputStream(), DependencyCheckReportDTO.class);


            // Print the CodeNarcReportDTO object
            printDependencyCheckReportDTO(dependencyCheckReportDTO);
            return dependencyCheckReportDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printDependencyCheckReportDTO(DependencyCheckReportDTO dependencyCheckReportDTO){
        System.out.println("Dependency Check Packages:");

        // Print package information
        for (DependencyDTO dependencyDTO : dependencyCheckReportDTO.getDependencies()) {
            System.out.println("File Name: " + dependencyDTO.getFileName());

            // Print file information
            for (VulnerabilitiesDTO vulnerabilitiesDTO : dependencyDTO.getVulnerabilities()) {
                System.out.println("Name: " + vulnerabilitiesDTO.getName());
                System.out.println("Severity: " + vulnerabilitiesDTO.getSeverity());
            }
        }
    }
}

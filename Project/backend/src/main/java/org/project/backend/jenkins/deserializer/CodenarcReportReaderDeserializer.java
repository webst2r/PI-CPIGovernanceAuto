package org.project.backend.jenkins.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.backend.jenkins.dto.codenarc.CodenarcReportDTO;
import org.project.backend.jenkins.dto.codenarc.PackageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CodenarcReportReaderDeserializer {
    private final ResourceLoader resourceLoader;

    @Value("${path.internal}")
    private String internalPath;
    public CodenarcReportDTO deserialize(String jobName) {
        ObjectMapper objectMapper = new ObjectMapper();

        String internalPath = this.internalPath + "workspace/"+ jobName+"/output.json";
        Path projectPath = Paths.get(internalPath);
        try {
            Resource resource = resourceLoader.getResource("file:" + projectPath.toString());
            // Read JSON file and parse it into a CodeNarcReportDTO object
            CodenarcReportDTO codenarcReportDTO = objectMapper.readValue(resource.getInputStream(), CodenarcReportDTO.class);

            // Get the packages with violations
            List<PackageDTO> packagesWithViolations = getPackageDTOsWithViolations(codenarcReportDTO.getPackages());

           codenarcReportDTO.setPackages(packagesWithViolations);

            return codenarcReportDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

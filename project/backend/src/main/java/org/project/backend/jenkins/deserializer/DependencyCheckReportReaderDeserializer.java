package org.project.backend.jenkins.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.backend.exception.BadRequestException;
import org.project.backend.exception.enumeration.ExceptionType;
import org.project.backend.jenkins.dto.dependecycheck.DependencyCheckReportDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class DependencyCheckReportReaderDeserializer {
    private final ResourceLoader resourceLoader;
    @Value("${path.internal}")
    private String internalPath;

    public DependencyCheckReportDTO deserialize(String jobName) {
        ObjectMapper objectMapper = new ObjectMapper();
        String internalPath = this.internalPath + "workspace/"+ jobName+"/dependency-check-report.json";
        Path projectPath = Paths.get(internalPath);

        try {
            Resource resource = resourceLoader.getResource("file:" + projectPath.toString());
            return objectMapper.readValue(resource.getInputStream(), DependencyCheckReportDTO.class);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read Dependency-Check report file", ExceptionType.FAILED_TO_READ_FILE);
        }
    }
}

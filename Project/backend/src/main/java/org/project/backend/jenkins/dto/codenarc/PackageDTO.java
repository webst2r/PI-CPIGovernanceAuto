package org.project.backend.jenkins.dto.codenarc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageDTO {
    @JsonProperty("path")
    private String path;

    @JsonProperty("totalFiles")
    private int totalFiles;

    @JsonProperty("filesWithViolations")
    private int filesWithViolations;

    @JsonProperty("priority1")
    private int priority1;

    @JsonProperty("priority2")
    private int priority2;

    @JsonProperty("priority3")
    private int priority3;

    @JsonProperty("files")
    private List<FileDTO> files;
}

package org.project.backend.jenkins.dto.codenarc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDTO {
    @JsonProperty("totalFiles")
    private Integer totalFiles;
    @JsonProperty("filesWithViolations")
    private Integer filesWithViolations;
    @JsonProperty("priority1")
    private Integer priority1;
    @JsonProperty("priority2")
    private Integer priority2;
    @JsonProperty("priority3")
    private Integer priority3;
}

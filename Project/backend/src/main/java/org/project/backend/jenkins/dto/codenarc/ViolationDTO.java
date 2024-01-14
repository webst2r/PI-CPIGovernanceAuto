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
public class ViolationDTO {
    @JsonProperty("ruleName")
    private String ruleName;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("lineNumber")
    private int lineNumber;

    @JsonProperty("sourceLine")
    private String sourceLine;

    @JsonProperty("message")
    private String message;
}

package org.project.backend.jenkins.dto.dependecycheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyDTO {
    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("vulnerabilities")
    private List<VulnerabilitiesDTO> vulnerabilities;
}

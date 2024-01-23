package org.project.backend.jenkins.dto.codenarc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodenarcReportDTO {
    @JsonProperty("summary")
    private SummaryDTO summary;

    @JsonProperty("packages")
    private List<PackageDTO> packages = new ArrayList<>();;
}

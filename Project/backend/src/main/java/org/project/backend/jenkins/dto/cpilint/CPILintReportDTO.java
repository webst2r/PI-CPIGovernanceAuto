package org.project.backend.jenkins.dto.cpilint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class CPILintReportDTO {
    private int numberOfIssues;
    private List<IssueDTO> issues  = new ArrayList<>();;
}

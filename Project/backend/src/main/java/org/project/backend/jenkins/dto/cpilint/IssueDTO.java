package org.project.backend.jenkins.dto.cpilint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueDTO {
    private String flowId;
    private String issue;
}


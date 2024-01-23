package org.project.backend.jenkins.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.backend.jenkins.dto.codenarc.CodenarcReportDTO;
import org.project.backend.jenkins.dto.cpilint.CPILintReportDTO;
import org.project.backend.jenkins.dto.dependecycheck.DependencyCheckReportDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    CodenarcReportDTO codenarcReport;
    DependencyCheckReportDTO dependencyCheckReport;
    CPILintReportDTO cpiLintReport;
}

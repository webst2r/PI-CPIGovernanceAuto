package org.project.backend.jenkins.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.backend.jenkins.dto.codenarc.CodenarcReportDTO;
import org.project.backend.jenkins.dto.dependecycheck.DependencyCheckReportDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    CodenarcReportDTO codenarcReport;
    DependencyCheckReportDTO dependencyCheckReport;
    //TODO: Add CPIlint report when converted to DTO

}

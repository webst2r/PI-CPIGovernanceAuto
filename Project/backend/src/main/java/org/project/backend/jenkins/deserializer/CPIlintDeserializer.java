package org.project.backend.jenkins.deserializer;

import lombok.RequiredArgsConstructor;
import org.project.backend.jenkins.dto.cpilint.CPILintReportDTO;
import org.project.backend.jenkins.dto.cpilint.IssueDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CPIlintDeserializer {
    private final ResourceLoader resourceLoader;
    @Value("${path.internal}")
    private String internalPath;
    public CPILintReportDTO deserialize(String jobName) {
        CPILintReportDTO cpilintReportDTO = new CPILintReportDTO();
        List<IssueDTO> issueList = new ArrayList<>();
        String internalPath = this.internalPath + "workspace/"+ jobName+"/cpilint.log";
        Path projectPath = Paths.get(internalPath);
        Resource resource = resourceLoader.getResource("file:" + projectPath.toString());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String logData;
            while ((logData = reader.readLine()) != null) {
                // Extract the number of issues
                Pattern issuesPattern = Pattern.compile("Issues found: (\\d+)");
                Matcher issuesMatcher = issuesPattern.matcher(logData);

                if (issuesMatcher.find()) {
                    cpilintReportDTO.setNumberOfIssues(Integer.parseInt(issuesMatcher.group(1)));
                }

                // Extract information about each issue
                Pattern logPattern = Pattern.compile("In iflow '(.*?)' \\(ID '(.*?)'\\): (.+)");
                Matcher logMatcher = logPattern.matcher(logData);

                if (logMatcher.find()) {
                    String iflowId = logMatcher.group(2);
                    String message = logMatcher.group(3);
                    issueList.add(new IssueDTO(iflowId, message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception according to your application's requirements
        }

        cpilintReportDTO.setIssues(issueList);
        return cpilintReportDTO;
    }


}

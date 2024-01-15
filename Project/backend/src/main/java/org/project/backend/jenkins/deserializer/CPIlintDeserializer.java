package org.project.backend.jenkins.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CPIlintDeserializer {
    private final ResourceLoader resourceLoader;

    public void deserialize() {
        ObjectMapper objectMapper = new ObjectMapper();

        //TODO: pass the name of the file as a parameter and change the resource directory to the correct one
        Resource resource = resourceLoader.getResource("classpath:" + "jenkins/cpilint_no_error.log");


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String logData;
            int numberOfIssues = 0;
            while ((logData = reader.readLine()) != null) {
                Pattern issuesPattern = Pattern.compile("Issues found: (\\d+)");
                Matcher issuesMatcher = issuesPattern.matcher(logData);

                if (issuesMatcher.find()) {
                    numberOfIssues = Integer.parseInt(issuesMatcher.group(1));
                    System.out.println("Number of issues found: " + numberOfIssues);
                }
                Pattern flowPattern = Pattern.compile("In iflow.*");
                Matcher flowMatcher = flowPattern.matcher(logData);

                while (flowMatcher.find()) {
                    String flowLine = flowMatcher.group();
                    System.out.println("Found issue line: " + flowLine);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception according to your application's requirements
        }


    }


}

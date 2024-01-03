package org.project.backend.repository.github.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.backend.credential.github.GithubCredentials;
import org.project.backend.exception.ExceptionConstants;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryRegisterRequest {
    @NotBlank(message = "name " + ExceptionConstants.REQUIRED)
    private String name;

    @NotBlank(message = "mainBranch " + ExceptionConstants.REQUIRED)
    private String mainBranch;

    @NotBlank(message = "secondaryBranches " + ExceptionConstants.REQUIRED)
    private List<String> secondaryBranches;

    @NotBlank(message = "githubCredentials " + ExceptionConstants.REQUIRED)
    private GithubCredentials githubCredentials;
}

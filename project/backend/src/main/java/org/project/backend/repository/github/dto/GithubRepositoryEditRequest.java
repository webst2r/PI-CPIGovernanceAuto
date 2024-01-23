package org.project.backend.repository.github.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.backend.exception.ExceptionConstants;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryEditRequest {
    @NotNull(message = "id " + ExceptionConstants.REQUIRED)
    private Long id;
    @NotBlank(message = "name " + ExceptionConstants.REQUIRED)
    private String name;

    @NotBlank(message = "mainBranch " + ExceptionConstants.REQUIRED)
    private String mainBranch;

    @NotBlank(message = "secondaryBranches " + ExceptionConstants.REQUIRED)
    private List<String> secondaryBranches;
}

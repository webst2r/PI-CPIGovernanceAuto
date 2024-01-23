package org.project.backend.credential.github.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.backend.exception.ExceptionConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubCredentialsRegisterRequest {
    @NotBlank(message = "name " + ExceptionConstants.REQUIRED)
    private String name;
    @NotBlank(message = "username " + ExceptionConstants.REQUIRED)
    private String username;
    @NotBlank(message = "accessToken " + ExceptionConstants.REQUIRED)
    private String accessToken;
}

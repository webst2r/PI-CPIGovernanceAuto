package org.project.backend.auth;

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
public class AuthenticationRequest {

    @NotBlank(message = "email " + ExceptionConstants.REQUIRED)
    private String email;
    @NotBlank(message = "password " + ExceptionConstants.REQUIRED)
    private String password;
}
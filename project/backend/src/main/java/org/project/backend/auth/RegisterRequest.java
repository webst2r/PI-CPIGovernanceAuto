package org.project.backend.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.backend.exception.ExceptionConstants;
import org.project.backend.user.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "email " + ExceptionConstants.REQUIRED)
    private String email;
    @NotBlank(message = "firstName " + ExceptionConstants.REQUIRED)
    private String firstName;
    @NotBlank(message = "lastName " + ExceptionConstants.REQUIRED)
    private String lastName;
    @NotBlank(message = "password " + ExceptionConstants.REQUIRED)
    private String password;
    @NotBlank(message = "role " + ExceptionConstants.REQUIRED)
    private Role role;
}
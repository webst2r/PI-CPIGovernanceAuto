package org.project.backend.credential.sap_cpi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.project.backend.exception.ExceptionConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialSapCpiEditRequest {
    @NotNull(message = "id " + ExceptionConstants.REQUIRED)
    private Long id;

    @NotBlank(message = "name " + ExceptionConstants.REQUIRED)
    private String name;

    @NotBlank(message = "baseUrl " + ExceptionConstants.REQUIRED)
    private String baseUrl;

    @NotBlank(message = "tokenUrl " + ExceptionConstants.REQUIRED)
    private String tokenUrl;

    @NotBlank(message = "clientId " + ExceptionConstants.REQUIRED)
    private String clientId;

    @NotBlank(message = "clientSecret " + ExceptionConstants.REQUIRED)
    private String clientSecret;
}

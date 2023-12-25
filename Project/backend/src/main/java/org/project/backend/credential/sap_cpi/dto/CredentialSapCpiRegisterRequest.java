package org.project.backend.credential.sap_cpi.dto;

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
public class CredentialSapCpiRegisterRequest {
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

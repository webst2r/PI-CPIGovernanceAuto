package org.project.backend.packages;

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
public class GetAllFlowsRequest {

    @NotBlank(message = "baseUrl " + ExceptionConstants.REQUIRED)
    private String baseUrl;
    @NotBlank(message = "packageName " + ExceptionConstants.REQUIRED)
    private String packageName;
}

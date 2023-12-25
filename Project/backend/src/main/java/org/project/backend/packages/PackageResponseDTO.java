package org.project.backend.packages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = PackageResponseDTODeserializer.class)
public class PackageResponseDTO {
    private IntegrationPackage _package;
}

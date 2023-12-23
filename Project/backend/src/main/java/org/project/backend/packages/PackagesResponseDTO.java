package org.project.backend.packages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = PackagesResponseDTODeserializer.class)
public class PackagesResponseDTO {
    private List<IntegrationPackage> results;
}

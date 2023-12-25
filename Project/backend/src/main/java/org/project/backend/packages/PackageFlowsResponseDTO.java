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
@JsonDeserialize(using = PackageFlowsResponseDTODeserializer.class)
public class PackageFlowsResponseDTO {
    private List<IntegrationFlow> results;
}

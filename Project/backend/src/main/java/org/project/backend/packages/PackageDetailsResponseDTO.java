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
@JsonDeserialize(using = PackageDetailsResponseDTODeserializer.class)
public class PackageDetailsResponseDTO {
    private List<IntegrationFlow> results;
}

package org.project.backend.jenkins.dto.codenarc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("violations")
    private List<ViolationDTO> violations = new ArrayList<>();;
}

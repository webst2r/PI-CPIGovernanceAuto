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
@JsonDeserialize(using = FlowResponseDTODeserializer.class)
public class FlowResponseDTO {
    private IntegrationFlow _flow;

    public void setFlow(IntegrationFlow flow) {
        this._flow = flow;
    }
}

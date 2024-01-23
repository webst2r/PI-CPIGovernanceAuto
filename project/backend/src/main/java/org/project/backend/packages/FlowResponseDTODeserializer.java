package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class FlowResponseDTODeserializer extends StdDeserializer<FlowResponseDTO> {

    public FlowResponseDTODeserializer() {
        this(null);
    }

    public FlowResponseDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public FlowResponseDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode dNode = node.get("d");

        IntegrationFlow flow = jp.getCodec().treeToValue(dNode, IntegrationFlow.class);

        FlowResponseDTO response = new FlowResponseDTO();
        response.setFlow(flow);

        return response;
    }
}

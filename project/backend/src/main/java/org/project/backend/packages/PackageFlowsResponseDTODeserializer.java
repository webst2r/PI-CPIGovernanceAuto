package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackageFlowsResponseDTODeserializer extends StdDeserializer<PackageFlowsResponseDTO> {

    public PackageFlowsResponseDTODeserializer() {
        this(null);
    }

    public PackageFlowsResponseDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PackageFlowsResponseDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode resultsNode = node.get("d").get("results");
        List<IntegrationFlow> results = new ArrayList<>();

        if (resultsNode.isArray()) {
            for (JsonNode resultNode : resultsNode) {
                IntegrationFlow flow = jp.getCodec().treeToValue(resultNode, IntegrationFlow.class);
                results.add(flow);
            }
        }

        PackageFlowsResponseDTO response = new PackageFlowsResponseDTO();
        response.setResults(results);
        return response;
    }
}

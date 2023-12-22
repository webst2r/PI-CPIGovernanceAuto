package org.project.backend.flows;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackagesResponseDTODeserializer extends StdDeserializer<PackagesResponseDTO> {

    public PackagesResponseDTODeserializer() {
        this(null);
    }

    public PackagesResponseDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PackagesResponseDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode resultsNode = node.get("d").get("results");
        List<IntegrationPackage> results = new ArrayList<>();

        if (resultsNode.isArray()) {
            for (JsonNode resultNode : resultsNode) {
                IntegrationPackage pkg = jp.getCodec().treeToValue(resultNode, IntegrationPackage.class);
                results.add(pkg);
            }
        }

        PackagesResponseDTO response = new PackagesResponseDTO();
        response.setResults(results);
        return response;
    }
}

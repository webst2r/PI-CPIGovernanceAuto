package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class PackageResponseDTODeserializer extends StdDeserializer<IntegrationPackage> {

    public PackageResponseDTODeserializer() {
        this(null);
    }

    public PackageResponseDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IntegrationPackage deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode dNode = node.get("d");

        // Extract fields from the "d" node
        String id = dNode.get("Id").asText();
        String name = dNode.get("Name").asText();
        String resourceId = dNode.get("ResourceId").asText();
        String description = dNode.get("Description").asText();
        String shortText = dNode.get("ShortText").asText();
        String version = dNode.get("Version").asText();
        String vendor = dNode.get("Vendor").asText();
        boolean partnerContent = dNode.get("PartnerContent").asBoolean();
        boolean updateAvailable = dNode.get("UpdateAvailable").asBoolean();
        String mode = dNode.get("Mode").asText();
        String supportedPlatform = dNode.get("SupportedPlatform").asText();
        String modifiedBy = dNode.get("ModifiedBy").asText();
        String creationDate = dNode.get("CreationDate").asText();
        String modifiedDate = dNode.get("ModifiedDate").asText();
        String createdBy = dNode.get("CreatedBy").asText();

        IntegrationPackage integrationPackage = new IntegrationPackage();
        integrationPackage.setId(id);
        integrationPackage.setName(name);
        integrationPackage.setResourceId(resourceId);
        integrationPackage.setDescription(description);
        integrationPackage.setShortText(shortText);
        integrationPackage.setVersion(version);
        integrationPackage.setVendor(vendor);
        integrationPackage.setPartnerContent(partnerContent);
        integrationPackage.setUpdateAvailable(updateAvailable);
        integrationPackage.setMode(mode);
        integrationPackage.setSupportedPlatform(supportedPlatform);
        integrationPackage.setModifiedBy(modifiedBy);
        integrationPackage.setCreationDate(creationDate);
        integrationPackage.setModifiedDate(modifiedDate);
        integrationPackage.setCreatedBy(createdBy);

        return integrationPackage;
    }
}

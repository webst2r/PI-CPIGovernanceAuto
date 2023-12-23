package org.project.backend.packages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationPackage {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("ResourceId")
    private String resourceId;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("ShortText")
    private String ShortText;
    @JsonProperty("Version")
    private String Version;
    @JsonProperty("Vendor")
    private String Vendor;
    @JsonProperty("PartnerContent")
    private boolean PartnerContent;
    @JsonProperty("UpdateAvailable")
    private boolean UpdateAvailable;
    @JsonProperty("Mode")
    private String Mode;
    @JsonProperty("SupportedPlatform")
    private String SupportedPlatform;
    @JsonProperty("ModifiedBy")
    private String ModifiedBy;
    @JsonProperty("CreationDate")
    private String CreationDate;
    @JsonProperty("ModifiedDate")
    private String ModifiedDate;
    @JsonProperty("CreatedBy")
    private String CreatedBy;
}

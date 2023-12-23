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
public class IntegrationFlow {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Version")
    private String version;

    @JsonProperty("PackageId")
    private String packageId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("CreatedAt")
    private String createdAt;

    @JsonProperty("ModifiedBy")
    private String modifiedBy;

    @JsonProperty("ModifiedAt")
    private String modifiedAt;

}

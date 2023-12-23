package org.project.backend.packages;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackagesResponse {

    private String message;


    public String getMessage() {
        return message;
    }

}

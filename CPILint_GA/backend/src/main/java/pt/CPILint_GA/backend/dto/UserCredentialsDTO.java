package pt.CPILint_GA.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.CPILint_GA.backend.utils.ExceptionConstants;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {
    @NotBlank(message = "email " + ExceptionConstants.REQUIRED)
    private String email;
    @NotBlank(message = "password " + ExceptionConstants.REQUIRED)
    private String password;

}


package pt.CPILint_GA.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.CPILint_GA.backend.utils.ExceptionConstants;
import pt.CPILint_GA.backend.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "email " + ExceptionConstants.REQUIRED)
    private String email;
    @NotBlank(message = "firstName " + ExceptionConstants.REQUIRED)
    private String firstName;
    @NotBlank(message = "lastName " + ExceptionConstants.REQUIRED)
    private String lastName;
    @NotBlank(message = "password " + ExceptionConstants.REQUIRED)
    private String password;


    public User toEntity() {
        return new User(this.firstName, this.lastName, this.email, this.password);
    }
}

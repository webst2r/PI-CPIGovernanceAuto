package pt.CPILint_GA.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.CPILint_GA.backend.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithTokenDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String token;

    public UserWithTokenDTO(User user, String token){
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.token = token;
    }
}

package pt.CPILint_GA.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.CPILint_GA.backend.dto.UserCredentialsDTO;
import pt.CPILint_GA.backend.dto.UserDTO;
import pt.CPILint_GA.backend.dto.UserWithTokenDTO;
import pt.CPILint_GA.backend.service.UserService;

import javax.validation.Valid;

@RepositoryRestController()
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("user/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserDTO userDTO) {
        var msg = userService.register(userDTO);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("user/login")
    public ResponseEntity<UserWithTokenDTO> login(@RequestBody @Valid UserCredentialsDTO userCredentialsDTO) {
        var userWithTokenDTO  = userService.login(userCredentialsDTO);
        return ResponseEntity.ok(userWithTokenDTO);
    }
}

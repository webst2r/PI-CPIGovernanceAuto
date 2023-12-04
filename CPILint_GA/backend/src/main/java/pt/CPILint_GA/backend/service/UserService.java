package pt.CPILint_GA.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.CPILint_GA.backend.dto.UserCredentialsDTO;
import pt.CPILint_GA.backend.dto.UserDTO;
import pt.CPILint_GA.backend.dto.UserWithTokenDTO;
import pt.CPILint_GA.backend.enumeration.ExceptionType;
import pt.CPILint_GA.backend.exception.BadRequestException;
import pt.CPILint_GA.backend.repository.UserRepository;
import pt.CPILint_GA.backend.security.jwt.JwtUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;


    public UserWithTokenDTO login(UserCredentialsDTO userCredentialsDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCredentialsDTO.getEmail(), userCredentialsDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        var user = userRepository.findByEmail(userCredentialsDTO.getEmail()).get();

        return new UserWithTokenDTO(user, jwt);
    }


    public String register(UserDTO userDTO){
        validateNewEmail(userDTO.getEmail());
        var user = userDTO.toEntity();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    private void validateNewEmail(String email) throws BadRequestException {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists", ExceptionType.EMAIL_ALREADY_EXISTS);
        }
    }
}

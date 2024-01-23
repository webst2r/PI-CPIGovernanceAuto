package org.project.backend.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.backend.config.JwtService;
import org.project.backend.exception.BadRequestException;
import org.project.backend.exception.enumeration.ExceptionType;
import org.project.backend.user.Role;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public String register(RegisterRequest request) {
        validateNewEmail(request.getEmail());
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return "User registered successfully!";
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        var user = userRepository.findByEmail(request.getEmail()).get();

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .build();
    }

    private void validateNewEmail(String email) throws BadRequestException {

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists", ExceptionType.EMAIL_ALREADY_EXISTS);
        }
    }
}

package org.project.backend.credential;

import org.project.backend.credential.Credential;
import org.project.backend.credential.CredentialService;
import org.project.backend.user.User;
import org.project.backend.user.UserRepository;
import org.project.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    private final UserService userService;

    @Autowired
    public CredentialController(CredentialService credentialService,  UserService userService) {
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostMapping("/registerCredentials")
    public ResponseEntity<String> registerCredentials(@RequestBody Credential credentialDto) {

        // Find or create a User object with the username (email)
        try {
            // Get the logged-in user's details
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails) {
                // Cast the principal to UserDetails to get user details
                UserDetails userDetails = (UserDetails) principal;
                User user = userService.findOrCreateUser(userDetails.getUsername());

                // Set the user in the credential object
                credentialDto.setUser(user);

                // Check if credentials with the same user_id exist
                Credential existingCredential = credentialService.findByUserId(user.getId());

                if (existingCredential != null) {
                    // Update the existing credential with new values
                    existingCredential.setBaseUrl(credentialDto.getBaseUrl());
                    existingCredential.setClientId(credentialDto.getClientId());
                    existingCredential.setClientSecret(credentialDto.getClientSecret());
                    existingCredential.setTokenUrl(credentialDto.getTokenUrl());

                    credentialService.updateCredentials(existingCredential);
                } else {
                    // Save the credentials if no existing record is found
                    credentialService.registerCredentials(credentialDto);
                }

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("Credentials registered successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("User not authenticated");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace to the console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error registering credentials");
        }
    }


}

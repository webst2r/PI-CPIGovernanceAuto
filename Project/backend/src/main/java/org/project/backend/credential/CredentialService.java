package org.project.backend.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;

    @Autowired
    public CredentialService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    public void registerCredentials(Credential credentialDto) {
        credentialRepository.save(credentialDto);
    }

    public void updateCredentials(Credential existingCredential) {
        // Assuming that Credential entity has appropriate setters for the fields
        // Update the existingCredential in the database
        credentialRepository.save(existingCredential);
    }

    public Credential findByUserId(Long id) {
        return credentialRepository.findByUserId(id);
    }
}

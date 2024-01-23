package org.project.backend.credential.sap_cpi;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.backend.credential.sap_cpi.dto.CredentialSapCpiEditRequest;
import org.project.backend.credential.sap_cpi.dto.CredentialSapCpiRegisterRequest;
import org.project.backend.exception.ResourceNotFoundException;
import org.project.backend.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CredentialSapCpiService {

    private final CredentialSapCpiRepository credentialSapCpiRepository;
    private final UserService userService;

    @Transactional
    public CredentialSapCpi registerCredentials(CredentialSapCpiRegisterRequest credentialsDTO) {
        var user = userService.findLoginUser();
        CredentialSapCpi credential = CredentialSapCpi.builder()
                .name(credentialsDTO.getName())
                .baseUrl(credentialsDTO.getBaseUrl())
                .tokenUrl(credentialsDTO.getTokenUrl())
                .clientId(credentialsDTO.getClientSecret())
                .clientSecret(credentialsDTO.getClientSecret())
                .user(user)
                .build();
       return credentialSapCpiRepository.save(credential);
    }

    @Transactional
    public CredentialSapCpi updateCredentials(CredentialSapCpiEditRequest credentialsDTO) {
        var user = userService.findLoginUser();
        CredentialSapCpi credentialSapCpi = credentialSapCpiRepository.findById(credentialsDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("CredentialSapCpi", "id", credentialsDTO.getId()));

        credentialSapCpi.setName(credentialsDTO.getName());
        credentialSapCpi.setBaseUrl(credentialsDTO.getBaseUrl());
        credentialSapCpi.setTokenUrl(credentialsDTO.getTokenUrl());
        credentialSapCpi.setClientId(credentialsDTO.getClientId());
        credentialSapCpi.setClientSecret(credentialsDTO.getClientSecret());
        return credentialSapCpiRepository.save(credentialSapCpi);
    }

    @Transactional
    public CredentialSapCpi findAllByUser() {
        var user = userService.findLoginUser();
        var opt = credentialSapCpiRepository.findByUser(user);
        return opt.orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        var user = userService.findLoginUser();
        user.setCredentialSapCpi(null);
        userService.save(user);
        credentialSapCpiRepository.deleteById(id);
    }
}

package org.project.backend.credential.jenkins;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.backend.credential.jenkins.dto.JenkinsCredentialsEditRequest;
import org.project.backend.credential.jenkins.dto.JenkinsCredentialsRegisterRequest;
import org.project.backend.exception.ResourceNotFoundException;
import org.project.backend.user.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JenkinsCredentialsService {

    private final JenkinsCredentialsRepository jenkinsCredentialsRepository;
    private final UserService userService;

    @Transactional
    public JenkinsCredentials save(JenkinsCredentialsRegisterRequest jenkinsCredentialsRegisterRequest) {
        var user = userService.findLoginUser();

        var jenkinsCredentials = JenkinsCredentials.builder()
                .name(jenkinsCredentialsRegisterRequest.getName())
                .username(jenkinsCredentialsRegisterRequest.getUsername())
                .accessToken(jenkinsCredentialsRegisterRequest.getAccessToken())
                .user(user)
                .build();

        return jenkinsCredentialsRepository.save(jenkinsCredentials);
    }

    @Transactional
    public JenkinsCredentials update(JenkinsCredentialsEditRequest jenkinsCredentialsEditRequest) {
        var user = userService.findLoginUser();
        var jenkinsCredentials = jenkinsCredentialsRepository.findById(jenkinsCredentialsEditRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("JenkinsCredentials", "id", jenkinsCredentialsEditRequest.getId()));
        jenkinsCredentials.setName(jenkinsCredentialsEditRequest.getName());
        jenkinsCredentials.setUsername(jenkinsCredentialsEditRequest.getUsername());
        jenkinsCredentials.setAccessToken(jenkinsCredentialsEditRequest.getAccessToken());

        return jenkinsCredentialsRepository.save(jenkinsCredentials);
    }

    @Transactional
    public JenkinsCredentials findByUser() {
        var user = userService.findLoginUser();
        var opt = jenkinsCredentialsRepository.findByUser(user);
        return opt.orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        var user = userService.findLoginUser();
        user.setJenkinsCredentials(null);
        userService.save(user);
        jenkinsCredentialsRepository.deleteById(id);
    }

}

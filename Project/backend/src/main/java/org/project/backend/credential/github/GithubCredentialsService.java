package org.project.backend.credential.github;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.backend.credential.github.dto.GithubCredentialsEditRequest;
import org.project.backend.credential.github.dto.GithubCredentialsRegisterRequest;
import org.project.backend.exception.ResourceNotFoundException;
import org.project.backend.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubCredentialsService {

    private final GithubCredentialsRepository githubCredentialsRepository;
    private final UserService userService;

    @Transactional
    public GithubCredentials save(GithubCredentialsRegisterRequest githubCredentialsRegisterRequest) {
       var user = userService.findLoginUser();

       var githubCredentials = GithubCredentials.builder()
               .name(githubCredentialsRegisterRequest.getName())
               .username(githubCredentialsRegisterRequest.getUsername())
               .accessToken(githubCredentialsRegisterRequest.getAccessToken())
               .user(user)
               .build();

       return githubCredentialsRepository.save(githubCredentials);
    }

    @Transactional
    public GithubCredentials update(GithubCredentialsEditRequest githubCredentialsEditRequest) {
        var user = userService.findLoginUser();
        var githubCredentials = githubCredentialsRepository.findById(githubCredentialsEditRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("GithubCredentials", "id", githubCredentialsEditRequest.getId()));
        githubCredentials.setName(githubCredentialsEditRequest.getName());
        githubCredentials.setUsername(githubCredentialsEditRequest.getUsername());
        githubCredentials.setAccessToken(githubCredentialsEditRequest.getAccessToken());

        return githubCredentialsRepository.save(githubCredentials);

    }

    @Transactional
    public GithubCredentials findByUser() {
        var user = userService.findLoginUser();
        var opt = githubCredentialsRepository.findByUser(user);
        return opt.orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        var user = userService.findLoginUser();
        user.setGithubCredentials(null);
        userService.save(user);
        githubCredentialsRepository.deleteById(id);
    }

}

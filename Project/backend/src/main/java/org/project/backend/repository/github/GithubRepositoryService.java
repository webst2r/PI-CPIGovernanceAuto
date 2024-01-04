package org.project.backend.repository.github;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.backend.credential.github.GithubCredentials;
import org.project.backend.credential.github.GithubCredentialsService;
import org.project.backend.exception.ResourceNotFoundException;
import org.project.backend.repository.github.dto.GithubRepositoryEditRequest;
import org.project.backend.repository.github.dto.GithubRepositoryRegisterRequest;
import org.project.backend.user.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubRepositoryService {

    private final GithubRepositoryRepository githubRepositoryRepository;
    private final UserService userService;
    private final GithubCredentialsService githubCredentialsService;

    @Transactional
    public GithubRepository save(GithubRepositoryRegisterRequest githubRepositoryRegisterRequest) {
        var githubCredentials = githubRepositoryRegisterRequest.getGithubCredentials();


        System.out.println("Repository name: " + githubRepositoryRegisterRequest.getName());
        System.out.println("Repository main branch: " + githubRepositoryRegisterRequest.getMainBranch());
        System.out.println("Repository secondary branches: " + githubRepositoryRegisterRequest.getSecondaryBranches());
        System.out.println("Repository credentials: " + githubRepositoryRegisterRequest.getGithubCredentials());

        var githubRepository = GithubRepository.builder()
                .name(githubRepositoryRegisterRequest.getName())
                .mainBranch(githubRepositoryRegisterRequest.getMainBranch())
                .secondaryBranches(githubRepositoryRegisterRequest.getSecondaryBranches())
                .credentials(githubCredentials)
                .build();

        return githubRepositoryRepository.save(githubRepository);
    }

    @Transactional
    public GithubRepository update(GithubRepositoryEditRequest githubRepositoryEditRequest) {
        var githubRepository = githubRepositoryRepository.findById(githubRepositoryEditRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("GithubRepository", "id", githubRepositoryEditRequest.getId()));

        githubRepository.setName(githubRepositoryEditRequest.getName());
        githubRepository.setMainBranch(githubRepositoryEditRequest.getMainBranch());
        githubRepository.setSecondaryBranches(githubRepositoryEditRequest.getSecondaryBranches());

        return githubRepositoryRepository.save(githubRepository);
    }

    @Transactional
    public void delete(Long id) {
        githubRepositoryRepository.deleteById(id);
    }

    @Transactional
    public GithubRepository findByCredentials() {
        GithubCredentials credentials = githubCredentialsService.findByUser();
        var opt = githubRepositoryRepository.findByCredentials(credentials);
        return opt.orElse(null);
    }
}

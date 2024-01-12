package org.project.backend.repository.github;

import org.project.backend.credential.github.GithubCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubRepositoryRepository extends JpaRepository<GithubRepository, Long> {

    Optional<GithubRepository> findByCredentials(GithubCredentials credentials);
}

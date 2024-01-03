package org.project.backend.credential.jenkins;

import org.project.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JenkinsCredentialsRepository extends JpaRepository<JenkinsCredentials, Long> {
    Optional<JenkinsCredentials> findByUser(User user);

    JenkinsCredentials findByUserId(Long id);
}

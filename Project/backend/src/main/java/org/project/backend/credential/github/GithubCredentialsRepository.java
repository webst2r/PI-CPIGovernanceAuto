package org.project.backend.credential.github;

import org.project.backend.credential.sap_cpi.CredentialSapCpi;
import org.project.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubCredentialsRepository extends JpaRepository<GithubCredentials, Long> {
    Optional<GithubCredentials> findByUser(User user);

    GithubCredentials findByUserId(Long id);
}

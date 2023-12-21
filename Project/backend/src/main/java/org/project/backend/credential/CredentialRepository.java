package org.project.backend.credential;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    Credential findByUserId(Long userId);
}

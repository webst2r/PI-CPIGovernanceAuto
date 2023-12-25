package org.project.backend.credential.sap_cpi;

import org.project.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CredentialSapCpiRepository extends JpaRepository<CredentialSapCpi, Long> {

    CredentialSapCpi findByUserId(Long userId);

    Optional<CredentialSapCpi> findByUser(User user);
}

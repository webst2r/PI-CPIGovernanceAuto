package org.project.backend.rulefiles;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuleFileRepository extends JpaRepository<RuleFile, Long> {
    Optional<Object> getByFileName(String fileName);
}

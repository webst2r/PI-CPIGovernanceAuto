package org.project.backend.configuration_files.codenarc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodenarcFileRepository extends JpaRepository<CodenarcFile, Long> {
    Optional<Object> getByFileName(String fileName);
}
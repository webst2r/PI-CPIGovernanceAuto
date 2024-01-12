package org.project.backend.configuration_files.codenarc;

import lombok.RequiredArgsConstructor;
import org.project.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodenarcFileService {
    private final CodenarcFileRepository codenarcFileRepository;

    public CodenarcFile saveCodenarcFile(String fileName, byte[] fileContent) {
        CodenarcFile codenarcFile = new CodenarcFile();
        codenarcFile.setFileName(fileName);
        codenarcFile.setFileContent(fileContent);
        return codenarcFileRepository.save(codenarcFile);
    }

    public CodenarcFile getCodenarcFile(Long fileId) {
        return codenarcFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("CodenarcFile", "id", fileId));
    }

    public CodenarcFile getCodenarcFileByName(String fileName) {
        return (CodenarcFile) codenarcFileRepository.getByFileName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("CodenarcFile", "fileName", fileName));
    }


    public List<CodenarcFile> getAllCodenarcFiles() {
        return codenarcFileRepository.findAll();
    }

    public boolean doesFileExist(String fileName) {
        return codenarcFileRepository.getByFileName(fileName).isPresent();
    }
}

package org.project.backend.configuration_files.cpi;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleFileService {

    private final RuleFileRepository ruleFileRepository;

    public RuleFile saveRuleFile(String fileName, byte[] fileContent) {
        RuleFile ruleFile = new RuleFile();
        ruleFile.setFileName(fileName);
        ruleFile.setFileContent(fileContent);
        return ruleFileRepository.save(ruleFile);
    }

    public RuleFile getRuleFile(Long fileId) {
        return ruleFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("RuleFile", "id", fileId));
    }

    public RuleFile getRuleFileByName(String fileName) {
        return (RuleFile) ruleFileRepository.getByFileName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("RuleFile", "fileName", fileName));
    }


    public List<RuleFile> getAllRuleFiles() {
        return ruleFileRepository.findAll();
    }

    public boolean doesFileExist(String fileName) {
        return ruleFileRepository.getByFileName(fileName).isPresent();
    }
}

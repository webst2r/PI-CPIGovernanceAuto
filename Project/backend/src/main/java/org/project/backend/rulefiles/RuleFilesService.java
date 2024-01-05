package org.project.backend.rulefiles;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleFilesService {

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

    public List<RuleFile> getAllRuleFiles() {
        return ruleFileRepository.findAll();
    }
}

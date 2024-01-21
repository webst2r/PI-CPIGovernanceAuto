package org.project.backend.configuration_files.cpi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rulefiles")
@RequiredArgsConstructor
public class RuleFileController {

        public final RuleFileService ruleFilesService;

        @PostMapping("/save")
        public ResponseEntity<RuleFile> uploadRuleFile(@RequestParam("file") MultipartFile file) throws IOException {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                byte[] fileContent = file.getBytes();

                RuleFile savedFile = ruleFilesService.saveRuleFile(fileName, fileContent);

                return ResponseEntity.ok(savedFile);
        }

        // delete
        @DeleteMapping("/delete/{fileId}")
        public ResponseEntity<RuleFile> deleteRuleFile(@PathVariable Long fileId) {
                RuleFile ruleFile = ruleFilesService.deleteRuleFile(fileId);
                return ResponseEntity.ok(ruleFile);
        }

        @GetMapping("/{fileId}")
        public ResponseEntity<byte[]> downloadRuleFile(@PathVariable Long fileId) {
                RuleFile ruleFile = ruleFilesService.getRuleFile(fileId);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(ruleFile.getFileName()).build());

                return new ResponseEntity<>(ruleFile.getFileContent(), headers, HttpStatus.OK);
        }

        @GetMapping("/all")
        public ResponseEntity<List<RuleFile>> getAllRuleFiles() {
                List<RuleFile> ruleFiles = ruleFilesService.getAllRuleFiles();
                return ResponseEntity.ok(ruleFiles);
        }

        @GetMapping("/exists/{fileName}")
        public ResponseEntity<Boolean> checkFileExists(@PathVariable String fileName) {
                boolean fileExists = ruleFilesService.doesFileExist(fileName);
                return ResponseEntity.ok(fileExists);
        }

}

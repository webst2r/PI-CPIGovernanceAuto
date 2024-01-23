package org.project.backend.configuration_files.codenarc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/codenarc")
@RequiredArgsConstructor
public class CodenarcFileController {
    private final CodenarcFileService codenarcFileService;

    @PostMapping("/save")
    public ResponseEntity<CodenarcFile> uploadCodenarcFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] fileContent = file.getBytes();

        CodenarcFile savedFile = codenarcFileService.saveCodenarcFile(fileName, fileContent);

        return ResponseEntity.ok(savedFile);
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Void> deleteCodenarcFile(@PathVariable Long fileId) {
        codenarcFileService.deleteCodenarcFile(fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadCodenarcFile(@PathVariable Long fileId) {
        CodenarcFile codenarcFile = codenarcFileService.getCodenarcFile(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(codenarcFile.getFileName()).build());

        return new ResponseEntity<>(codenarcFile.getFileContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CodenarcFile>> getAllCodenarcFiles() {
        List<CodenarcFile> codenarcFiles = codenarcFileService.getAllCodenarcFiles();
        return ResponseEntity.ok(codenarcFiles);
    }

    @GetMapping("/exists/{fileName}")
    public ResponseEntity<Boolean> checkFileExists(@PathVariable String fileName) {
        boolean fileExists = codenarcFileService.doesFileExist(fileName);
        return ResponseEntity.ok(fileExists);
    }
}

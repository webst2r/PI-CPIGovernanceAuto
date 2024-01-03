package org.project.backend.credential.jenkins;

import lombok.RequiredArgsConstructor;
import org.project.backend.credential.jenkins.dto.JenkinsCredentialsEditRequest;
import org.project.backend.credential.jenkins.dto.JenkinsCredentialsRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credentials/jenkins")
@RequiredArgsConstructor
public class JenkinsCredentialsController {
    public final JenkinsCredentialsService jenkinsCredentialsService;

    @PostMapping("/save")
    public ResponseEntity<JenkinsCredentials> saveCredentials(@RequestBody JenkinsCredentialsRegisterRequest jenkinsCredentialsRegisterRequest) {
        var credentials =  jenkinsCredentialsService.save(jenkinsCredentialsRegisterRequest);
        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/update")
    public ResponseEntity<JenkinsCredentials> editCredentials(@RequestBody JenkinsCredentialsEditRequest jenkinsCredentialsEditRequest) {
        var credentials =  jenkinsCredentialsService.update(jenkinsCredentialsEditRequest);
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/key")
    public ResponseEntity<JenkinsCredentials> listCredentials() {
        var credentials =  jenkinsCredentialsService.findByUser();
        return ResponseEntity.ok(credentials);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCredentials(@RequestParam Integer id) {
        jenkinsCredentialsService.delete(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }

}

package org.project.backend.credential.github;

import lombok.RequiredArgsConstructor;
import org.project.backend.credential.github.dto.GithubCredentialsEditRequest;
import org.project.backend.credential.github.dto.GithubCredentialsRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credentials/github")
@RequiredArgsConstructor
public class GithubCredentialsController {

    public final GithubCredentialsService githubCredentialsService;

    @PostMapping("/save")
    public ResponseEntity<GithubCredentials> saveCredentials(@RequestBody GithubCredentialsRegisterRequest githubCredentialsRegisterRequest) {
        var credentials =  githubCredentialsService.save(githubCredentialsRegisterRequest);
        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/update")
    public ResponseEntity<GithubCredentials> editCredentials(@RequestBody GithubCredentialsEditRequest githubCredentialsEditRequest) {
        var credentials =  githubCredentialsService.update(githubCredentialsEditRequest);
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/key")
    public ResponseEntity<GithubCredentials> listCredentials() {
        var credentials =  githubCredentialsService.findByUser();
        return ResponseEntity.ok(credentials);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCredentials(@RequestParam Integer id) {
        githubCredentialsService.delete(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }



}

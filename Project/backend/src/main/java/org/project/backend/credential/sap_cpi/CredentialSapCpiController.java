package org.project.backend.credential.sap_cpi;

import lombok.RequiredArgsConstructor;
import org.project.backend.credential.sap_cpi.dto.CredentialSapCpiEditRequest;
import org.project.backend.credential.sap_cpi.dto.CredentialSapCpiRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credentials/sap_cpi")
@RequiredArgsConstructor
public class CredentialSapCpiController {

    private final CredentialSapCpiService credentialSapCpiService;

    @PostMapping("/save")
    public ResponseEntity<CredentialSapCpi> registerCredentials(@RequestBody CredentialSapCpiRegisterRequest credentialDto) {
        var credentials =  credentialSapCpiService.registerCredentials(credentialDto);
        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/update")
    public ResponseEntity<CredentialSapCpi> editCredentials(@RequestBody CredentialSapCpiEditRequest credentialsEditRequest) {
        var credentials =  credentialSapCpiService.updateCredentials(credentialsEditRequest);
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("key")
    public ResponseEntity<CredentialSapCpi> listCredentials() {
        var credentials =  credentialSapCpiService.findAllByUser();
        return ResponseEntity.ok(credentials);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCredentials(@RequestParam("id") Integer id) {
        credentialSapCpiService.delete(Long.valueOf(id));
        return ResponseEntity.noContent().build();
    }


}

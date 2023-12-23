package org.project.backend.packages;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/packages")
public class PackagesController {

    private final PackagesService packagesService;

    @Autowired
    public PackagesController(PackagesService packagesService) {
        this.packagesService = packagesService;
    }


    @GetMapping("/getPackages")
    public ResponseEntity<PackagesResponseDTO> getPackages() throws JsonProcessingException {
        PackagesResponseDTO response = packagesService.obtainSecuredResource();
        return ResponseEntity.ok(response);
    }



    @GetMapping("/getPackageDetails/{id}")
    public ResponseEntity<PackageDetailsResponseDTO> getPackageDetails(@PathVariable("id") String packageId) throws JsonProcessingException {
        PackageDetailsResponseDTO response = packagesService.getPackageDetails(packageId);

        return ResponseEntity.ok(response);
    }



}

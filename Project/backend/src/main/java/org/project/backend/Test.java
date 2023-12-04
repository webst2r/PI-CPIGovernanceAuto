package org.project.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("api/test")
    public String getApostasCountByUser(){
        return "Teste";
    }
}

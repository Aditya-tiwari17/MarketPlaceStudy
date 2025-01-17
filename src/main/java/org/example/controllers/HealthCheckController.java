package org.example.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class HealthCheckController {
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok().body("Health OK!");
    }
}

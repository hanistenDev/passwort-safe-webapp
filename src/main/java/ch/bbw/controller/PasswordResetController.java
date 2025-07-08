package ch.bbw.controller;

import ch.bbw.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reset")
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestReset(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Benutzername ist erforderlich");
        }

        try {
            String token = passwordResetService.createResetToken(username);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReset(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token ist erforderlich");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Neues Passwort ist erforderlich");
        }

        try {
            passwordResetService.confirmReset(token, newPassword);
            return ResponseEntity.ok("Passwort erfolgreich zurückgesetzt");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
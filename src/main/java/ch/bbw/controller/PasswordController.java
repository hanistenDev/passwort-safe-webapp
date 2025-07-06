package ch.bbw.controller;

import ch.bbw.model.PasswordEntry;
import ch.bbw.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {
    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping
    public List<PasswordEntry> getAll(@AuthenticationPrincipal UserDetails user) {
        String key = user.getPassword();
        return passwordService.getAll(user.getUsername(), key);
    }

    @PostMapping
    public PasswordEntry save(@RequestBody PasswordEntry entry, @AuthenticationPrincipal UserDetails user) {
        entry.setOwnerUsername(user.getUsername());
        return passwordService.save(entry, user.getPassword());
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body,
                                                @AuthenticationPrincipal UserDetails user) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Neues Passwort darf nicht leer sein.");
        }

        passwordService.changeMasterPassword(user.getUsername(), newPassword);
        return ResponseEntity.ok("Passwort erfolgreich geändert.");
    }
}

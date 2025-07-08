package ch.bbw.controller;

import ch.bbw.model.AppUser;
import ch.bbw.model.PasswordEntry;
import ch.bbw.repository.AppUserRepository;
import ch.bbw.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    public PasswordController(PasswordService passwordService, 
                            PasswordEncoder passwordEncoder,
                            AppUserRepository userRepository) {
        this.passwordService = passwordService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails user) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Altes und neues Passwort müssen angegeben werden.");
        }

        // Get the user to validate the old password
        AppUser appUser = userRepository.findById(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Validate old password against stored hash
        if (!passwordEncoder.matches(oldPassword, appUser.getPasswordHash())) {
            return ResponseEntity.badRequest().body("Das aktuelle Passwort ist nicht korrekt.");
        }

        try {
            passwordService.changeMasterPassword(user.getUsername(), newPassword);
            return ResponseEntity.ok("Passwort erfolgreich geändert.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

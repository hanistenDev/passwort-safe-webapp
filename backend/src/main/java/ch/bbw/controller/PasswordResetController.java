package ch.bbw.controller;

import ch.bbw.service.PasswordService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reset")
public class PasswordResetController {

    private final PasswordService passwordService;


    private Map<String, String> tokenStore = new HashMap<>();

    public PasswordResetController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // Schritt 1: Reset-Token anfordern
    @PostMapping("/request-token")
    public Map<String, String> requestToken(@RequestBody Map<String, String> body) {
        String username = body.get("username");

        // Dummy Token
        String token = "123456";
        tokenStore.put(username, token);


        return Map.of("token", token);
    }

   // Neues Masterpasswort setzen
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String token = body.get("token");
        String newMasterKey = body.get("newPassword");

        // Validierung des Tokens
        if (!token.equals(tokenStore.get(username))) {
            throw new RuntimeException("Ungültiger Token");
        }

        // Setze neues Masterpasswort
        passwordService.changeMasterPassword(newMasterKey);

        // Entferne den Token nach Benutzung
        tokenStore.remove(username);

        return Map.of("message", "Masterpasswort erfolgreich geändert");
    }
}

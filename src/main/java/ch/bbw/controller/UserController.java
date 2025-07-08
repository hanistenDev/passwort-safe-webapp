package ch.bbw.controller;

import ch.bbw.model.AppUser;
import ch.bbw.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final CustomUserDetailsService userService;

    public UserController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Benutzername und Passwort sind erforderlich");
            }
            
            AppUser createdUser = userService.registerNewUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler bei der Registrierung: " + e.getMessage());
        }
    }
}

class RegistrationRequest {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 
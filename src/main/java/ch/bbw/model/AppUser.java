package ch.bbw.model;

import jakarta.persistence.*;

@Entity
public class AppUser {
    @Id
    private String username;
    private String passwordHash; // Gehashtes Masterpasswort
    private String encryptionKey; // Verschlüsselungsschlüssel für Passwörter
    private String email; // Email für Password Reset

    // Getters und Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEncryptionKey() { return encryptionKey; }
    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
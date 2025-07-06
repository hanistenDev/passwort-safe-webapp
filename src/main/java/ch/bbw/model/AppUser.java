package ch.bbw.model;

import jakarta.persistence.*;

@Entity
public class AppUser {
    @Id
    private String username;
    private String passwordHash; // Gehashtes Masterpasswort

    // Getters und Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
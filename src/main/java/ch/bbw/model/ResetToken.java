package ch.bbw.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ResetToken {
    @Id
    private String token;
    private String username;
    private LocalDateTime expiryDate;
    private boolean used;
    private int attempts;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}


package ch.bbw.service;

import ch.bbw.model.AppUser;
import ch.bbw.model.ResetToken;
import ch.bbw.repository.AppUserRepository;
import ch.bbw.repository.ResetTokenRepository;
import ch.bbw.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final ResetTokenRepository tokenRepository;
    private final AppUserRepository userRepository;
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${reset.token.expiry.minutes:15}")
    private int tokenExpiryMinutes;

    public PasswordResetService(ResetTokenRepository tokenRepository,
                              AppUserRepository userRepository,
                              PasswordService passwordService,
                              PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String createResetToken(String username) {
        // Validate user exists
        AppUser user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

        // Generate a secure random token
        String token = UUID.randomUUID().toString();

        // Create and save reset token
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUsername(username);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(tokenExpiryMinutes));
        resetToken.setUsed(false);
        resetToken.setAttempts(0);
        tokenRepository.save(resetToken);

        // Zum merken: In a real application, you would send this token via email

        return token;
    }

    @Transactional
    public void confirmReset(String token, String newPassword) {
        ResetToken resetToken = tokenRepository.findById(token)
                .orElseThrow(() -> new IllegalArgumentException("Ungültiger oder abgelaufener Token"));

        // Validate token
        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token ist abgelaufen");
        }
        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token wurde bereits verwendet");
        }
        if (resetToken.getAttempts() >= 3) {
            throw new IllegalArgumentException("Zu viele Versuche");
        }

        try {
            // Get the user
            AppUser user = userRepository.findById(resetToken.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

            // Update the user's password and re-encrypt all passwords
            passwordService.changeMasterPassword(user.getUsername(), newPassword);

            // Mark token as used
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
        } catch (Exception e) {
            resetToken.setAttempts(resetToken.getAttempts() + 1);
            tokenRepository.save(resetToken);
            throw new IllegalArgumentException("Fehler beim Zurücksetzen des Passworts");
        }
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}

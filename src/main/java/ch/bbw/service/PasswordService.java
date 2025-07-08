package ch.bbw.service;

import ch.bbw.model.AppUser;
import ch.bbw.model.PasswordEntry;
import ch.bbw.repository.AppUserRepository;
import ch.bbw.repository.PasswordRepository;
import ch.bbw.util.AESUtil;
import ch.bbw.util.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordRepository passwordRepository,
                           AppUserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.passwordRepository = passwordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<PasswordEntry> getAll(String username, String encryptionKey) {
        final String key = encryptionKey != null ? encryptionKey :
                userRepository.findById(username)
                        .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"))
                        .getEncryptionKey();

        return passwordRepository.findByOwnerUsername(username).stream()
                .map(e -> {
                    String decryptedPassword = AESUtil.decrypt(e.getPassword(), key);
                    if (decryptedPassword == null) {
                        throw new RuntimeException("Fehler beim Entschlüsseln des Passworts");
                    }
                    e.setPassword(decryptedPassword);
                    return e;
                })
                .collect(Collectors.toList());
    }

    public PasswordEntry save(PasswordEntry entry, String encryptionKey) {
        if (entry == null || entry.getPassword() == null) {
            throw new IllegalArgumentException("Passwort-Eintrag oder Passwort fehlt");
        }

        final String key = encryptionKey != null ? encryptionKey :
                userRepository.findById(entry.getOwnerUsername())
                        .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"))
                        .getEncryptionKey();

        // Encrypt the password
        String encryptedPassword = AESUtil.encrypt(entry.getPassword(), key);
        entry.setPassword(encryptedPassword);
        
        return passwordRepository.save(entry);
    }

    public void changeMasterPassword(String username, String newPassword) {
        // Validate the new password
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validatePassword(newPassword);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Ungültiges Passwort: " + String.join(", ", validationResult.getErrors()));
        }

        AppUser user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        // Get all password entries for re-encryption
        List<PasswordEntry> entries = passwordRepository.findByOwnerUsername(username);
        String oldKey = user.getEncryptionKey();

        // Hash the new password for storage
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Update user's password hash and encryption key
        user.setPasswordHash(hashedPassword);
        user.setEncryptionKey(newPassword); // Use unhashed password for encryption
        userRepository.save(user);

        // Re-encrypt all password entries with new key
        for (PasswordEntry entry : entries) {
            String decrypted = AESUtil.decrypt(entry.getPassword(), oldKey);
            entry.setPassword(AESUtil.encrypt(decrypted, newPassword));
            passwordRepository.save(entry);
        }
    }
}

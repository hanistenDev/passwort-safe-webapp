package ch.bbw.service;

import ch.bbw.model.AppUser;
import ch.bbw.repository.AppUserRepository;
import ch.bbw.util.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerUser(AppUser user) {
        // Check if username already exists
        if (userRepository.findById(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Benutzername existiert bereits");
        }

        // Validate the password
        String rawPassword = user.getPasswordHash(); // This contains the raw password at this point
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validatePassword(rawPassword);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Ungültiges Passwort: " + String.join(", ", validationResult.getErrors()));
        }

        // Hash the password before saving
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setEncryptionKey(rawPassword); // Use unhashed password as encryption key
        
        return userRepository.save(user);
    }
} 
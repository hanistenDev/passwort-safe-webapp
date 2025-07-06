package ch.bbw.service;

import ch.bbw.model.AppUser;
import ch.bbw.model.PasswordEntry;
import ch.bbw.repository.AppUserRepository;
import ch.bbw.repository.PasswordRepository;
import ch.bbw.util.AESUtil;
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

    public List<PasswordEntry> getAll(String username, String key) {
        return passwordRepository.findByOwnerUsername(username).stream()
                .map(e -> {
                    e.setPassword(AESUtil.decrypt(e.getPassword(), key));
                    return e;
                })
                .collect(Collectors.toList());
    }

    public PasswordEntry save(PasswordEntry entry, String key) {
        entry.setPassword(AESUtil.encrypt(entry.getPassword(), key));
        return passwordRepository.save(entry);
    }

    public void changeMasterPassword(String username, String newPassword) {
        AppUser user = userRepository.findById(username).orElse(null);
        if (user != null) {
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPasswordHash(hashedPassword);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Benutzer nicht gefunden");
        }
    }
}

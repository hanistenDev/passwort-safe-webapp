package ch.bbw.service;

import ch.bbw.model.AppUser;
import ch.bbw.repository.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public AppUser registerNewUser(String username, String password) {
        if (userRepository.findById(username).isPresent()) {
            throw new IllegalArgumentException("Benutzername existiert bereits");
        }

        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPasswordHash(password); // Store original password without hashing
        newUser.setEncryptionKey(password); // Store original password as encryption key
        return userRepository.save(newUser);
    }
} 
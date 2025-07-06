package ch.bbw.service;

import ch.bbw.model.ResetToken;
import ch.bbw.repository.ResetTokenRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetTokenService {
    private final ResetTokenRepository repo;

    public ResetTokenService(ResetTokenRepository repo) {
        this.repo = repo;
    }

    public ResetToken create(String username) {
        ResetToken token = new ResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUsername(username);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        return repo.save(token);
    }

    public boolean validate(String username, String token) {
        Optional<ResetToken> result = repo.findById(token);
        return result.filter(t -> t.getUsername().equals(username) && t.getExpiryDate().isAfter(LocalDateTime.now())).isPresent();
    }
}

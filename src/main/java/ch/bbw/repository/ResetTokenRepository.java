package ch.bbw.repository;

import ch.bbw.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
    void deleteByExpiryDateBefore(LocalDateTime date);
}

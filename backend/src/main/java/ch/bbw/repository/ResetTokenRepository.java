package ch.bbw.repository;

import ch.bbw.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
}

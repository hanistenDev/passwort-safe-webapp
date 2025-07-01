package ch.bbw.repository;

import ch.bbw.model.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<PasswordEntry, Long> {
}

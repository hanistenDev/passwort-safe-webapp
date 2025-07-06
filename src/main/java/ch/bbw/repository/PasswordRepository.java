package ch.bbw.repository;

import ch.bbw.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PasswordRepository extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByOwnerUsername(String ownerUsername);
}

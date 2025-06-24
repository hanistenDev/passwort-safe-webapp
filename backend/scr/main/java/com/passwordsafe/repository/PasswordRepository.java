package com.passwordsafe.repository;

import com.passwordsafe.model.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<PasswordEntry, Long> {
}

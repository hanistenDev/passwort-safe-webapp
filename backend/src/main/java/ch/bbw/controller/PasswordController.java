package ch.bbw.controller;

import ch.bbw.model.PasswordEntry;
import ch.bbw.service.PasswordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/api/passwords")
    public List<PasswordEntry> getAll() {
        return passwordService.getAll();
    }

    @PostMapping("/api/passwords")
    public PasswordEntry save(@RequestBody PasswordEntry entry) {
        return passwordService.save(entry);
    }

    @DeleteMapping("/api/passwords/{id}")
    public void delete(@PathVariable Long id) {
        passwordService.delete(id);
    }
}

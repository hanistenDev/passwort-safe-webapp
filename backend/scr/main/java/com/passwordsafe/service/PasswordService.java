package com.passwordsafe.service;

import com.passwordsafe.model.PasswordEntry;
import com.passwordsafe.repository.PasswordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKeySpec;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;

    @Value("${app.master.key:zzz}") 
    private String masterKey;

    public PasswordService(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    public List<PasswordEntry> getAll() {
        return passwordRepository.findAll().stream()
                .map(entry -> {
                    entry.setPassword(decrypt(entry.getPassword()));
                    return entry;
                })
                .collect(Collectors.toList());
    }

    public PasswordEntry save(PasswordEntry entry) {
        entry.setPassword(encrypt(entry.getPassword()));
        return passwordRepository.save(entry);
    }

    public void delete(Long id) {
        passwordRepository.deleteById(id);
    }

    private String encrypt(String data) {
        try {
            SecretKeySpec keySpec = createKey(masterKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return bytesToHex(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei der Verschlüsselung", e);
        }
    }

    private String decrypt(String data) {
        try {
            SecretKeySpec keySpec = createKey(masterKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return new String(cipher.doFinal(hexToBytes(data)));
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei der Entschlüsselung", e);
        }
    }

    private SecretKeySpec createKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key.getBytes("UTF-8"));
        return new SecretKeySpec(keyBytes, "AES");
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    private byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
}

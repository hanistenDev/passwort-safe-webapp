package ch.bbw.service;

import ch.bbw.model.PasswordEntry;
import ch.bbw.repository.PasswordRepository;
import ch.bbw.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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
                    entry.setPassword(AESUtil.decrypt(entry.getPassword()));
                    return entry;
                })
                .collect(Collectors.toList());
    }

    public PasswordEntry save(PasswordEntry entry) {
        entry.setPassword(AESUtil.encrypt(entry.getPassword()));
        return passwordRepository.save(entry);
    }

    public void delete(Long id) {
        passwordRepository.deleteById(id);
    }

    public void changeMasterPassword(String newMasterKey) {
        List<PasswordEntry> all = passwordRepository.findAll();

        for (PasswordEntry entry : all) {
            String plain = AESUtil.decrypt(entry.getPassword());
            entry.setPassword(encrypt(plain, newMasterKey));
        }

        this.masterKey = newMasterKey;
        passwordRepository.saveAll(all);
    }

    private String encrypt(String data, String key) {
        try {
            SecretKeySpec keySpec = AESUtil.createKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return AESUtil.bytesToHex(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei Verschlüsselung", e);
        }
    }
}

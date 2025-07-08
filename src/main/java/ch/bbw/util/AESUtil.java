package ch.bbw.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
    private static byte[] getKeyBytes(String key) throws Exception {
        // Use SHA-256 to get a consistent key length
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(key.getBytes("UTF-8"));
        // Use first 16 bytes (128 bits) for AES key
        return Arrays.copyOf(hash, 16);
    }

    public static String encrypt(String data, String key) {
        if (data == null || key == null) {
            throw new IllegalArgumentException("Data und Schlüssel dürfen nicht null sein");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException("Verschlüsselungsfehler: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String encrypted, String key) {
        if (encrypted == null || key == null) {
            throw new IllegalArgumentException("Verschlüsselte Daten und Schlüssel dürfen nicht null sein");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Entschlüsselungsfehler: " + e.getMessage(), e);
        }
    }
}
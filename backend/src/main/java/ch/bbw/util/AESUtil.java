package ch.bbw.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec key = createKey("zzz"); // Standard-Key
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return bytesToHex(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei Verschlüsselung", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec key = createKey("zzz"); // Standard-Key
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(hexToBytes(encryptedText)));
        } catch (Exception e) {
            throw new RuntimeException("Fehler bei Entschlüsselung", e);
        }
    }

    public static SecretKeySpec createKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key.getBytes(CHARSET));
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) (
                    (Character.digit(hex.charAt(i), 16) << 4) +
                            Character.digit(hex.charAt(i + 1), 16)
            );
        }
        return bytes;
    }
}

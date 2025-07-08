package ch.bbw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator {
    private static final int MIN_LENGTH = 12;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static ValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        // Check minimum length
        if (password == null || password.length() < MIN_LENGTH) {
            errors.add("Das Passwort muss mindestens " + MIN_LENGTH + " Zeichen lang sein");
        }

        // Check for uppercase letters
        if (password != null && !UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Das Passwort muss mindestens einen Großbuchstaben enthalten");
        }

        // Check for lowercase letters
        if (password != null && !LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Das Passwort muss mindestens einen Kleinbuchstaben enthalten");
        }

        // Check for digits
        if (password != null && !DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Das Passwort muss mindestens eine Zahl enthalten");
        }

        // Check for special characters
        if (password != null && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Das Passwort muss mindestens ein Sonderzeichen enthalten (!@#$%^&*(),.?\":{}|<>)");
        }

        // Check for common patterns
        if (password != null) {
            if (hasRepeatingCharacters(password)) {
                errors.add("Das Passwort darf keine sich wiederholenden Zeichen enthalten (z.B. 'aaa', '111')");
            }

            if (hasSequentialCharacters(password)) {
                errors.add("Das Passwort darf keine sequenziellen Zeichen enthalten (z.B. 'abc', '123')");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private static boolean hasRepeatingCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSequentialCharacters(String password) {
        String lowerPassword = password.toLowerCase();
        String sequences = "abcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < sequences.length() - 2; i++) {
            String forward = sequences.substring(i, i + 3);
            String reverse = new StringBuilder(forward).reverse().toString();
            
            if (lowerPassword.contains(forward) || lowerPassword.contains(reverse)) {
                return true;
            }
        }
        return false;
    }
} 
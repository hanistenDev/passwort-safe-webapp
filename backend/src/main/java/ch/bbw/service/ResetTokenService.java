package ch.bbw.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ResetTokenService {


    private final Map<String, String> tokenStore = new HashMap<>();

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(username, token);
        return token;
    }

    public boolean isValidToken(String username, String token) {
        return token.equals(tokenStore.get(username));
    }

    public void invalidateToken(String username) {
        tokenStore.remove(username);
    }
}

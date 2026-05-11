package com.jimmy.etfquant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String phoneNumber) {
        try {
            long now = Instant.now().toEpochMilli();
            long exp = now + expirationMs;

            Map<String, Object> header = Map.of(
                    "alg", "HS256",
                    "typ", "JWT"
            );

            Map<String, Object> payload = Map.of(
                    "sub", phoneNumber,
                    "user_id", userId,
                    "iat", now,
                    "exp", exp
            );

            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsBytes(payload));

            String data = encodedHeader + "." + encodedPayload;
            String signature = sign(data);

            return data + "." + signature;
        } catch (Exception error) {
            throw new IllegalStateException("Failed to generate JWT token.", error);
        }
    }

    public String validateAndGetSubject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format.");
            }

            String data = parts[0] + "." + parts[1];
            String expectedSignature = sign(data);

            if (!constantTimeEquals(expectedSignature, parts[2])) {
                throw new IllegalArgumentException("Invalid JWT signature.");
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            Map<?, ?> payload = objectMapper.readValue(payloadBytes, Map.class);

            long exp = ((Number) payload.get("exp")).longValue();
            if (Instant.now().toEpochMilli() > exp) {
                throw new IllegalArgumentException("JWT token expired.");
            }

            return payload.get("sub").toString();
        } catch (Exception error) {
            throw new IllegalArgumentException("Invalid JWT token.", error);
        }
    }

    private String sign(String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        hmac.init(key);
        return base64UrlEncode(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private boolean constantTimeEquals(String a, String b) {
        return java.security.MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }
}
package com.example.demo.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * Sinh Access Token dạng JWT thật sự - chứa claims (userId, role)
     * để mỗi request không cần query lại DB để biết role của user.
     */
    public String generateAccessToken(Long userId, String role) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Refresh Token KHÔNG cần là JWT - nó chỉ đóng vai trò một "key" ngẫu nhiên
     * để tra cứu trong Redis. Không mã hoá payload gì bên trong.
     * Dùng SecureRandom thay vì UUID.randomUUID() vì UUID không được thiết kế
     * cho mục đích bảo mật (không đảm bảo entropy đủ mạnh chống đoán trước).
     */
    public String generateRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Verify chữ ký + hạn dùng của Access Token.
     * Nếu token sai chữ ký hoặc hết hạn, thư viện JJWT sẽ tự throw exception
     * (JwtException / ExpiredJwtException) - ta bắt exception này ở Filter, không ở đây.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()

                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractAllClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
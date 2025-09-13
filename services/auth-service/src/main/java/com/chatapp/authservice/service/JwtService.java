package com.chatapp.authservice.service;

import com.chatapp.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String phoneNumber) {
        return generateToken(phoneNumber, new HashMap<>());
    }

    public String generateToken(String phoneNumber, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(final User user) {
        final Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("roles", "USER"); // You can add roles later

        return this.generateToken(user.getPhoneNumber(), claims);
    }

    public String getPhoneNumberFromToken(final String token) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getUserIdFromToken(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Date getExpirationDateFromToken(final String token) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    public boolean isTokenExpired(final String token) {
        final Date expiration = this.getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }
}

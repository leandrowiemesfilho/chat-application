package com.chatapp.security.service;

import com.chatapp.security.model.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public String generateToken(final JwtUser user) {
        final Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.id());
        claims.put("name", user.name());
        claims.put("email", user.email());
        claims.put("phoneNumber", user.phoneNumber());

        return Jwts.builder()
                .setSubject(user.phoneNumber())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.jwtExpirationMs))
                .signWith(this.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(final String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(final String token) {
        return !this.isTokenExpired(token);
    }

    public Date getExpirationDateFromToken(final String token) {
        final Claims claims = this.validateToken(token);

        return claims.getExpiration();
    }

    public boolean isTokenExpired(final String token) {
        final Date expiration = this.getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }

    public String extractUsername(final String token) {
        final Claims claims = this.validateToken(token);

        return claims.getSubject();
    }

    public String extractUserId(final String token) {
        final Claims claims = this.validateToken(token);

        return claims.get("userId").toString();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
    }
}

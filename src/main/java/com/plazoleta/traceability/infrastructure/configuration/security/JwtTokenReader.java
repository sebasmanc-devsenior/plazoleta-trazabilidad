package com.plazoleta.traceability.infrastructure.configuration.security;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Valida la firma del JWT con la clave compartida y lee de él el id y el rol del usuario. */
@Component
public class JwtTokenReader {
    private final SecretKey secretKey;

    public JwtTokenReader(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<AuthenticatedUser> read(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(authorizationHeader.substring(7).trim()).getPayload();
            Object rawUserId = claims.get("userId");
            Long userId = rawUserId == null ? Long.valueOf(claims.getSubject()) : Long.valueOf(rawUserId.toString());
            String role = claims.get("role", String.class);
            if (userId <= 0 || role == null) {
                return Optional.empty();
            }
            return Optional.of(new AuthenticatedUser(userId, role));
        } catch (Exception invalidToken) {
            return Optional.empty();
        }
    }
}

package com.zti.doctorreservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Narzędzie do generowania i walidacji tokenów JWT (JSON Web Token).
 * Tokeny są podpisywane algorytmem HMAC-SHA256 przy użyciu klucza
 * konfigurowanego przez właściwość {@code jwt.secret}.
 */
@Component
public class JwtUtil {

    /** Klucz podpisu JWT wczytywany z konfiguracji aplikacji. */
    @Value("${jwt.secret}")
    private String secret;

    /** Czas ważności tokenu w milisekundach. */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Tworzy klucz kryptograficzny HMAC-SHA256 na podstawie skonfigurowanego sekretu.
     *
     * @return klucz podpisu
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generuje token JWT dla podanego użytkownika.
     *
     * @param userDetails dane użytkownika (używana jest nazwa użytkownika jako subject)
     * @return podpisany token JWT
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Buduje i podpisuje token JWT.
     *
     * @param claims  dodatkowe dane do umieszczenia w tokenie
     * @param subject nazwa użytkownika jako identyfikator (subject)
     * @return podpisany token JWT w formacie Base64
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Wyciąga nazwę użytkownika z tokenu JWT.
     *
     * @param token token JWT
     * @return nazwa użytkownika (subject)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Sprawdza czy token jest ważny dla danego użytkownika.
     *
     * @param token       token JWT do weryfikacji
     * @param userDetails dane użytkownika do porównania
     * @return {@code true} jeśli token jest ważny i należy do użytkownika
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Sprawdza czy token JWT wygasł.
     *
     * @param token token JWT
     * @return {@code true} jeśli token wygasł
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Wyciąga dowolne pole z claims tokenu JWT.
     *
     * @param <T>            typ zwracanej wartości
     * @param token          token JWT
     * @param claimsResolver funkcja mapująca claims na oczekiwaną wartość
     * @return wartość wyciągnięta z claims
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}

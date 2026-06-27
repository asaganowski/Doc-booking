package com.zti.doctorreservation.controller;

import com.zti.doctorreservation.dto.AuthRequest;
import com.zti.doctorreservation.dto.AuthResponse;
import com.zti.doctorreservation.dto.RegisterRequest;
import com.zti.doctorreservation.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Kontroler REST obsługujący rejestrację i logowanie użytkowników.
 * Endpointy w tej klasie są publiczne -- nie wymagają tokenu JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Rejestruje nowego użytkownika w systemie.
     *
     * @param request dane rejestracyjne z walidacją Bean Validation
     * @return token JWT oraz rola i nazwa zarejestrowanego użytkownika
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Uwierzytelnia użytkownika i zwraca token JWT.
     *
     * @param request dane logowania: nazwa użytkownika i hasło
     * @return token JWT oraz rola i nazwa zalogowanego użytkownika
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

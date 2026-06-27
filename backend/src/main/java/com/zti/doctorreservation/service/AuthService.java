package com.zti.doctorreservation.service;

import com.zti.doctorreservation.dto.AuthRequest;
import com.zti.doctorreservation.dto.AuthResponse;
import com.zti.doctorreservation.dto.RegisterRequest;
import com.zti.doctorreservation.model.Patient;
import com.zti.doctorreservation.model.User;
import com.zti.doctorreservation.repository.PatientRepository;
import com.zti.doctorreservation.repository.UserRepository;
import com.zti.doctorreservation.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serwis odpowiedzialny za rejestrację i logowanie użytkowników.
 * Po pomyślnym uwierzytelnieniu generuje token JWT zwracany do klienta.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Rejestruje nowego użytkownika w systemie.
     * Dla roli PATIENT automatycznie tworzy powiązany profil pacjenta.
     *
     * @param request dane rejestracyjne (login, email, hasło, imię, nazwisko)
     * @return token JWT oraz dane zalogowanego użytkownika
     * @throws IllegalArgumentException gdy nazwa użytkownika lub email są już zajęte
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.PATIENT);
        userRepository.save(user);

        if (user.getRole() == User.Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(user);
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setPhoneNumber(request.getPhoneNumber());
            patientRepository.save(patient);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    /**
     * Uwierzytelnia użytkownika i zwraca token JWT.
     *
     * @param request dane logowania (login i hasło)
     * @return token JWT oraz dane zalogowanego użytkownika
     * @throws org.springframework.security.core.AuthenticationException gdy dane są nieprawidłowe
     */
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}

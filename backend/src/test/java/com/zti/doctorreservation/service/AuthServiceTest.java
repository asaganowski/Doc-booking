package com.zti.doctorreservation.service;

import com.zti.doctorreservation.dto.AuthResponse;
import com.zti.doctorreservation.dto.RegisterRequest;
import com.zti.doctorreservation.model.Patient;
import com.zti.doctorreservation.model.User;
import com.zti.doctorreservation.repository.PatientRepository;
import com.zti.doctorreservation.repository.UserRepository;
import com.zti.doctorreservation.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("jan.kowalski");
        registerRequest.setEmail("jan@example.com");
        registerRequest.setPassword("haslo123");
        registerRequest.setFirstName("Jan");
        registerRequest.setLastName("Kowalski");
        registerRequest.setRole(User.Role.PATIENT);
    }

    @Test
    void register_ShouldCreatePatientAndReturnToken() {
        when(userRepository.existsByUsername("jan.kowalski")).thenReturn(false);
        when(userRepository.existsByEmail("jan@example.com")).thenReturn(false);
        when(passwordEncoder.encode("haslo123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("jan.kowalski")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("jan.kowalski");
        assertThat(response.getRole()).isEqualTo("PATIENT");

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void register_ShouldThrow_WhenUsernameExists() {
        when(userRepository.existsByUsername("jan.kowalski")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already taken");

        verify(userRepository, never()).save(any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("jan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
    }
}

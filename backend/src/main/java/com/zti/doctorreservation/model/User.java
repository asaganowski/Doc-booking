package com.zti.doctorreservation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encja reprezentująca użytkownika systemu.
 * Użytkownik może pełnić rolę pacjenta, lekarza lub administratora.
 * Hasło przechowywane jest w postaci zaszyfrowanej (BCrypt).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"password", "doctor", "patient", "hibernateLazyInitializer"})
public class User {

    /** Unikalny identyfikator użytkownika generowany automatycznie. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unikalna nazwa użytkownika używana do logowania. */
    @NotBlank
    @Column(unique = true)
    private String username;

    /** Adres e-mail użytkownika, musi być unikalny. */
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    /** Hasło użytkownika w formie zaszyfrowanej (BCrypt). */
    @NotBlank
    private String password;

    /** Rola użytkownika określająca uprawnienia w systemie. */
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Profil lekarza powiązany z tym kontem (tylko dla roli DOCTOR). */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private Doctor doctor;

    /** Profil pacjenta powiązany z tym kontem (tylko dla roli PATIENT). */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private Patient patient;

    /**
     * Dostępne role użytkowników w systemie.
     */
    public enum Role {
        /** Pacjent -- może przeglądać lekarzy i rezerwować wizyty. */
        PATIENT,
        /** Lekarz -- może zarządzać swoimi terminami. */
        DOCTOR,
        /** Administrator -- pełny dostęp do systemu. */
        ADMIN
    }
}

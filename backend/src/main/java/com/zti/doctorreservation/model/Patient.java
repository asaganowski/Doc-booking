package com.zti.doctorreservation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Encja reprezentująca pacjenta w systemie rezerwacji.
 * Pacjent jest powiązany z kontem użytkownika i może posiadać wiele wizyt.
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"user", "appointments", "hibernateLazyInitializer"})
public class Patient {

    /** Unikalny identyfikator pacjenta. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Imię pacjenta. */
    private String firstName;

    /** Nazwisko pacjenta. */
    private String lastName;

    /** Data urodzenia pacjenta. */
    private LocalDate dateOfBirth;

    /** Numer telefonu kontaktowego pacjenta. */
    private String phoneNumber;

    /** Konto użytkownika powiązane z tym pacjentem. */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Lista wszystkich wizyt pacjenta. */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    /**
     * Zwraca pełne imię i nazwisko pacjenta.
     *
     * @return imię i nazwisko oddzielone spacją
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

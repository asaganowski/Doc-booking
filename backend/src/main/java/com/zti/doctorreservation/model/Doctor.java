package com.zti.doctorreservation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Encja reprezentująca lekarza w systemie rezerwacji.
 * Lekarz jest powiązany z kontem użytkownika i posiada listę dostępnych terminów.
 */
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"user", "timeSlots", "hibernateLazyInitializer"})
public class Doctor {

    /** Unikalny identyfikator lekarza. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Imię lekarza. */
    private String firstName;

    /** Nazwisko lekarza. */
    private String lastName;

    /** Specjalizacja medyczna lekarza (np. Kardiologia, Pediatria). */
    private String specialization;

    /** Numer telefonu kontaktowego lekarza. */
    private String phoneNumber;

    /** Konto użytkownika powiązane z tym lekarzem. */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Lista wszystkich terminów zdefiniowanych przez lekarza. */
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

    /**
     * Zwraca pełne imię i nazwisko lekarza.
     *
     * @return imię i nazwisko oddzielone spacją
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

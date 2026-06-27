package com.zti.doctorreservation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Encja reprezentująca wizytę lekarską.
 * Wiązana jest z konkretnym pacjentem, lekarzem i slotem czasowym.
 * Po anulowaniu wizyty powiązany slot wraca do statusu AVAILABLE.
 */
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
public class Appointment {

    /** Unikalny identyfikator wizyty. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Pacjent, który zarezerwował wizytę. */
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    /** Lekarz, u którego odbywa się wizyta. */
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    /** Slot czasowy przypisany do tej wizyty. */
    @OneToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    /** Aktualny status wizyty. Domyślnie SCHEDULED. */
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    /** Opcjonalne notatki pacjenta dotyczące wizyty. */
    private String notes;

    /** Data i czas utworzenia rezerwacji. */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Możliwe statusy wizyty lekarskiej.
     */
    public enum AppointmentStatus {
        /** Wizyta zaplanowana -- oczekuje na realizację. */
        SCHEDULED,
        /** Wizyta zakończona. */
        COMPLETED,
        /** Wizyta anulowana przez pacjenta lub lekarza. */
        CANCELLED
    }
}

package com.zti.doctorreservation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Encja reprezentująca slot czasowy (dostępny termin wizyty) lekarza.
 * Status slotu zmienia się w trakcie procesu rezerwacji i jest rozgłaszany
 * przez WebSocket do wszystkich podłączonych klientów.
 */
@Entity
@Table(name = "time_slots")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"appointment", "hibernateLazyInitializer"})
public class TimeSlot {

    /** Unikalny identyfikator slotu. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Data i godzina rozpoczęcia wizyty. */
    private LocalDateTime startTime;

    /** Data i godzina zakończenia wizyty. */
    private LocalDateTime endTime;

    /** Aktualny status slotu. Domyślnie AVAILABLE. */
    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.AVAILABLE;

    /** Lekarz, do którego należy ten termin. */
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    /** Wizyta powiązana z tym slotem (gdy status == BOOKED). */
    @OneToOne(mappedBy = "timeSlot", cascade = CascadeType.ALL)
    private Appointment appointment;

    /**
     * Możliwe statusy slotu czasowego.
     */
    public enum SlotStatus {
        /** Termin wolny -- można zarezerwować. */
        AVAILABLE,
        /** Ktoś właśnie rezerwuje -- widoczne dla innych pacjentów przez WebSocket. */
        BEING_RESERVED,
        /** Termin zajęty -- wizyta potwierdzona. */
        BOOKED
    }
}

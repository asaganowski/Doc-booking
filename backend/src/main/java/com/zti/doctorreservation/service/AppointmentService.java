package com.zti.doctorreservation.service;

import com.zti.doctorreservation.dto.AppointmentRequest;
import com.zti.doctorreservation.model.Appointment;
import com.zti.doctorreservation.model.Patient;
import com.zti.doctorreservation.model.TimeSlot;
import com.zti.doctorreservation.model.User;
import com.zti.doctorreservation.repository.AppointmentRepository;
import com.zti.doctorreservation.repository.PatientRepository;
import com.zti.doctorreservation.repository.TimeSlotRepository;
import com.zti.doctorreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serwis obsługujący logikę biznesową rezerwacji wizyt lekarskich.
 * Po każdej zmianie statusu slotu deleguje rozgłoszenie aktualizacji
 * do {@link TimeSlotService}, który wysyła wiadomość przez WebSocket.
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final TimeSlotService timeSlotService;

    /**
     * Rezerwuje wizytę dla aktualnie zalogowanego pacjenta.
     * Slot musi mieć status AVAILABLE lub BEING_RESERVED.
     * Po pomyślnej rezerwacji slot zmienia status na BOOKED.
     *
     * @param request dane rezerwacji: ID slotu i opcjonalne notatki
     * @return zapisana wizyta
     * @throws IllegalStateException    gdy profil pacjenta nie istnieje lub slot jest zajęty
     * @throws IllegalArgumentException gdy slot o podanym ID nie istnieje
     */
    @Transactional
    public Appointment book(AppointmentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Patient profile not found"));

        TimeSlot slot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found"));

        if (slot.getStatus() != TimeSlot.SlotStatus.AVAILABLE && slot.getStatus() != TimeSlot.SlotStatus.BEING_RESERVED) {
            throw new IllegalStateException("Slot is not available for booking");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(slot.getDoctor());
        appointment.setTimeSlot(slot);
        appointment.setNotes(request.getNotes());

        Appointment saved = appointmentRepository.save(appointment);
        timeSlotService.markAsBooked(slot.getId());
        return saved;
    }

    /**
     * Anuluje wizytę i zwalnia zajęty slot czasowy.
     * Slot wraca do statusu AVAILABLE i staje się dostępny dla innych pacjentów.
     *
     * @param appointmentId ID wizyty do anulowania
     * @return zaktualizowana wizyta ze statusem CANCELLED
     * @throws IllegalArgumentException gdy wizyta o podanym ID nie istnieje
     */
    @Transactional
    public Appointment cancel(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        timeSlotService.markAsAvailable(appointment.getTimeSlot().getId());
        return appointmentRepository.save(appointment);
    }

    /**
     * Zwraca listę wszystkich wizyt aktualnie zalogowanego pacjenta.
     *
     * @return lista wizyt pacjenta posortowana domyślnie przez bazę
     */
    public List<Appointment> getMyAppointments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Patient patient = patientRepository.findByUserId(user.getId()).orElseThrow();
        return appointmentRepository.findByPatientId(patient.getId());
    }

    /**
     * Zwraca listę wszystkich wizyt przypisanych do danego lekarza.
     *
     * @param doctorId ID lekarza
     * @return lista wizyt lekarza
     */
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
}

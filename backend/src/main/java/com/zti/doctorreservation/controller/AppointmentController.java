package com.zti.doctorreservation.controller;

import com.zti.doctorreservation.dto.AppointmentRequest;
import com.zti.doctorreservation.model.Appointment;
import com.zti.doctorreservation.service.AppointmentService;
import com.zti.doctorreservation.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST zarządzający wizytami lekarskimi.
 * Wszystkie endpointy wymagają uwierzytelnienia JWT.
 * Dostęp do poszczególnych operacji jest ograniczony rolami PATIENT i DOCTOR.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TimeSlotService timeSlotService;

    /**
     * Tworzy nową rezerwację wizyty dla zalogowanego pacjenta.
     *
     * @param request dane rezerwacji: ID slotu i opcjonalne notatki
     * @return zapisana wizyta
     */
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Appointment> book(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.book(request));
    }

    /**
     * Zwraca listę wizyt aktualnie zalogowanego pacjenta.
     *
     * @return lista wizyt pacjenta
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<Appointment>> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    /**
     * Anuluje wizytę o podanym ID i zwalnia powiązany slot.
     *
     * @param id ID wizyty do anulowania
     * @return zaktualizowana wizyta ze statusem CANCELLED
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<Appointment> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancel(id));
    }

    /**
     * Zwraca listę wszystkich wizyt przypisanych do danego lekarza.
     *
     * @param doctorId ID lekarza
     * @return lista wizyt lekarza
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId));
    }

    /**
     * Oznacza slot jako aktualnie rezerwowany (BEING_RESERVED).
     * Wywołanie tego endpointu powoduje natychmiastowe powiadomienie
     * innych pacjentów przez WebSocket.
     *
     * @param slotId ID slotu do oznaczenia
     * @return 200 OK po pomyślnym oznaczeniu
     */
    @PostMapping("/slots/{slotId}/reserve")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> markBeingReserved(@PathVariable Long slotId) {
        timeSlotService.markAsBeingReserved(slotId);
        return ResponseEntity.ok().build();
    }
}

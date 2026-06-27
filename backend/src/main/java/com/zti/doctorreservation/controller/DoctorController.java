package com.zti.doctorreservation.controller;

import com.zti.doctorreservation.model.Doctor;
import com.zti.doctorreservation.model.TimeSlot;
import com.zti.doctorreservation.dto.TimeSlotRequest;
import com.zti.doctorreservation.repository.DoctorRepository;
import com.zti.doctorreservation.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST zarządzający danymi lekarzy i ich terminami.
 * Endpointy odczytu są dostępne dla wszystkich uwierzytelnionych użytkowników.
 * Operacje zapisu (dodawanie terminów) wymagają roli DOCTOR.
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final TimeSlotService timeSlotService;

    /**
     * Zwraca listę wszystkich lekarzy zarejestrowanych w systemie.
     *
     * @return lista lekarzy
     */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    /**
     * Zwraca profil aktualnie zalogowanego lekarza.
     *
     * @param auth dane uwierzytelnienia z kontekstu Spring Security
     * @return profil lekarza lub 404 gdy lekarz nie istnieje
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Doctor> getMyProfile(Authentication auth) {
        return doctorRepository.findAll().stream()
                .filter(d -> d.getUser() != null &&
                        d.getUser().getUsername().equals(auth.getName()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Zwraca dane lekarza o podanym ID.
     *
     * @param id ID lekarza
     * @return dane lekarza lub 404 gdy nie istnieje
     */
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Zwraca lekarzy według specjalizacji.
     *
     * @param spec nazwa specjalizacji (np. "Kardiologia")
     * @return lista lekarzy o danej specjalizacji
     */
    @GetMapping("/specialization/{spec}")
    public ResponseEntity<List<Doctor>> getBySpecialization(@PathVariable String spec) {
        return ResponseEntity.ok(doctorRepository.findBySpecialization(spec));
    }

    /**
     * Zwraca dostępne (wolne) terminy danego lekarza.
     *
     * @param id ID lekarza
     * @return lista slotów ze statusem AVAILABLE
     */
    @GetMapping("/{id}/slots")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(@PathVariable Long id) {
        return ResponseEntity.ok(timeSlotService.getAvailableSlots(id));
    }

    /**
     * Zwraca wszystkie terminy danego lekarza (wszystkie statusy).
     * Dostępne wyłącznie dla lekarza i administratora.
     *
     * @param id ID lekarza
     * @return pełna lista terminów
     */
    @GetMapping("/{id}/slots/all")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<TimeSlot>> getAllSlots(@PathVariable Long id) {
        return ResponseEntity.ok(timeSlotService.getAllSlots(id));
    }

    /**
     * Dodaje nowy termin wizyty dla wskazanego lekarza.
     *
     * @param id      ID lekarza
     * @param request dane terminu: czas rozpoczęcia i zakończenia
     * @return utworzony slot ze statusem AVAILABLE
     */
    @PostMapping("/{id}/slots")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<TimeSlot> addSlot(@PathVariable Long id, @RequestBody TimeSlotRequest request) {
        return ResponseEntity.ok(timeSlotService.createSlot(id, request));
    }
}

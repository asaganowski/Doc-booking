package com.zti.doctorreservation.service;

import com.zti.doctorreservation.dto.TimeSlotRequest;
import com.zti.doctorreservation.model.Doctor;
import com.zti.doctorreservation.model.TimeSlot;
import com.zti.doctorreservation.repository.DoctorRepository;
import com.zti.doctorreservation.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serwis zarządzający slotami czasowymi lekarzy.
 * Każda zmiana statusu slotu jest automatycznie rozgłaszana przez WebSocket
 * do wszystkich klientów subskrybujących temat {@code /topic/slots/{doctorId}}.
 */
@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Tworzy nowy dostępny slot czasowy dla wskazanego lekarza.
     *
     * @param doctorId ID lekarza, któremu przypisywany jest slot
     * @param request  dane slotu: czas rozpoczęcia i zakończenia
     * @return utworzony slot ze statusem AVAILABLE
     * @throws IllegalArgumentException gdy lekarz o podanym ID nie istnieje
     */
    @Transactional
    public TimeSlot createSlot(Long doctorId, TimeSlotRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        TimeSlot slot = new TimeSlot();
        slot.setDoctor(doctor);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(TimeSlot.SlotStatus.AVAILABLE);

        TimeSlot saved = timeSlotRepository.save(slot);
        broadcastSlotUpdate(doctorId, saved);
        return saved;
    }

    /**
     * Zwraca listę dostępnych slotów (status AVAILABLE) dla danego lekarza.
     *
     * @param doctorId ID lekarza
     * @return lista wolnych terminów
     */
    public List<TimeSlot> getAvailableSlots(Long doctorId) {
        return timeSlotRepository.findByDoctorIdAndStatus(doctorId, TimeSlot.SlotStatus.AVAILABLE);
    }

    /**
     * Zwraca wszystkie sloty lekarza niezależnie od statusu.
     *
     * @param doctorId ID lekarza
     * @return pełna lista terminów lekarza
     */
    public List<TimeSlot> getAllSlots(Long doctorId) {
        return timeSlotRepository.findByDoctorId(doctorId);
    }

    /**
     * Oznacza slot jako aktualnie rezerwowany (BEING_RESERVED).
     * Informacja jest rozgłaszana przez WebSocket, dzięki czemu inni pacjenci
     * widzą w czasie rzeczywistym, że ktoś właśnie rezerwuje ten termin.
     *
     * @param slotId ID slotu do oznaczenia
     * @throws IllegalArgumentException gdy slot nie istnieje
     * @throws IllegalStateException    gdy slot nie jest w statusie AVAILABLE
     */
    @Transactional
    public void markAsBeingReserved(Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        if (slot.getStatus() != TimeSlot.SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is not available");
        }
        slot.setStatus(TimeSlot.SlotStatus.BEING_RESERVED);
        timeSlotRepository.save(slot);
        broadcastSlotUpdate(slot.getDoctor().getId(), slot);
    }

    /**
     * Oznacza slot jako zajęty (BOOKED) po potwierdzeniu rezerwacji.
     *
     * @param slotId ID slotu do oznaczenia
     */
    @Transactional
    public void markAsBooked(Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId).orElseThrow();
        slot.setStatus(TimeSlot.SlotStatus.BOOKED);
        timeSlotRepository.save(slot);
        broadcastSlotUpdate(slot.getDoctor().getId(), slot);
    }

    /**
     * Zwalnia slot i przywraca mu status AVAILABLE.
     * Wywoływane po anulowaniu wizyty.
     *
     * @param slotId ID slotu do zwolnienia
     */
    @Transactional
    public void markAsAvailable(Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId).orElseThrow();
        slot.setStatus(TimeSlot.SlotStatus.AVAILABLE);
        timeSlotRepository.save(slot);
        broadcastSlotUpdate(slot.getDoctor().getId(), slot);
    }

    /**
     * Wysyła zaktualizowany slot przez WebSocket do wszystkich subskrybentów tematu.
     *
     * @param doctorId ID lekarza (część adresu tematu)
     * @param slot     zaktualizowany slot do rozgłoszenia
     */
    private void broadcastSlotUpdate(Long doctorId, TimeSlot slot) {
        messagingTemplate.convertAndSend("/topic/slots/" + doctorId, slot);
    }
}

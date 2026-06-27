package com.zti.doctorreservation.repository;

import com.zti.doctorreservation.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByDoctorIdAndStatus(Long doctorId, TimeSlot.SlotStatus status);

    @Query("SELECT t FROM TimeSlot t WHERE t.doctor.id = :doctorId AND t.startTime >= :from AND t.startTime <= :to")
    List<TimeSlot> findByDoctorIdAndTimeRange(Long doctorId, LocalDateTime from, LocalDateTime to);

    List<TimeSlot> findByDoctorId(Long doctorId);
}

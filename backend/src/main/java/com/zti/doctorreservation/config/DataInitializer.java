package com.zti.doctorreservation.config;

import com.zti.doctorreservation.model.Doctor;
import com.zti.doctorreservation.model.TimeSlot;
import com.zti.doctorreservation.model.User;
import com.zti.doctorreservation.repository.DoctorRepository;
import com.zti.doctorreservation.repository.TimeSlotRepository;
import com.zti.doctorreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername("dr.kowalski")) {
            log.info("Data already initialized, skipping.");
            return;
        }

        log.info("Initializing sample data...");

        createDoctor("dr.kowalski", "kowalski@przychodnia.pl", "Jan", "Kowalski", "Kardiologia", "123-456-789");
        createDoctor("dr.nowak", "nowak@przychodnia.pl", "Anna", "Nowak", "Pediatria", "987-654-321");
        createDoctor("dr.wisniewski", "wisniewski@przychodnia.pl", "Piotr", "Wiśniewski", "Ortopedia", "555-111-222");

        log.info("Sample data initialized: 3 doctors with time slots.");
    }

    private void createDoctor(String username, String email, String firstName, String lastName,
                              String specialization, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("haslo123"));
        user.setRole(User.Role.DOCTOR);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setSpecialization(specialization);
        doctor.setPhoneNumber(phone);
        doctorRepository.save(doctor);

        generateSlots(doctor);
    }

    private void generateSlots(Doctor doctor) {
        LocalDate today = LocalDate.now();
        List<LocalTime> hours = List.of(
                LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0),
                LocalTime.of(11, 0), LocalTime.of(12, 0), LocalTime.of(13, 0)
        );

        for (int day = 1; day <= 7; day++) {
            LocalDate date = today.plusDays(day);
            if (date.getDayOfWeek().getValue() >= 6) continue; // skip weekends

            for (LocalTime hour : hours) {
                TimeSlot slot = new TimeSlot();
                slot.setDoctor(doctor);
                slot.setStartTime(LocalDateTime.of(date, hour));
                slot.setEndTime(LocalDateTime.of(date, hour.plusMinutes(30)));
                slot.setStatus(TimeSlot.SlotStatus.AVAILABLE);
                timeSlotRepository.save(slot);
            }
        }
    }
}

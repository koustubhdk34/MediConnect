package com.mediconnect.service;

import com.mediconnect.dto.AppointmentRequestDto;
import com.mediconnect.dto.AppointmentResponseDto;
import com.mediconnect.exception.DoctorOverloadedException;
import com.mediconnect.model.Appointment;
import com.mediconnect.model.AppointmentStatus;
import com.mediconnect.model.Doctor;
import com.mediconnect.model.User;
import com.mediconnect.repository.AppointmentRepository;
import com.mediconnect.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int MAX_APPOINTMENTS_PER_DAY = 5;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserService userService;

    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        LocalDateTime time = request.getAppointmentTime();
        if (time.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past");
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        LocalDate day = time.toLocalDate();
        long count = appointmentRepository.countByDoctorAndDay(doctor, day);
        if (count >= MAX_APPOINTMENTS_PER_DAY) {
            throw new DoctorOverloadedException("Doctor already has maximum appointments for this day");
        }

        User patient = getCurrentUser();

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentTime(time)
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // NEW CODE (With the status)
return new AppointmentResponseDto(
    appointment.getId(),
    appointment.getPatient().getId(),
    appointment.getPatient().getFullName(),
    appointment.getDoctor().getName(),
    appointment.getAppointmentTime(),
    appointment.getStatus() // <--- Add this!
);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsForCurrentPatient() {
        User patient = getCurrentUser();
        return appointmentRepository.findByPatientOrderByAppointmentTimeAsc(patient).stream()
                .map(a -> new AppointmentResponseDto(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getDoctor().getSpecialization(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.getByUsername(username);
    }
}


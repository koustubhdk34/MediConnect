package com.mediconnect.service;

import com.mediconnect.dto.AdminAppointmentDto;
import com.mediconnect.dto.AdminStatsDto;
import com.mediconnect.model.Appointment;
import com.mediconnect.model.AppointmentStatus;
import com.mediconnect.repository.AppointmentRepository;
import com.mediconnect.repository.DoctorRepository;
import com.mediconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public AdminStatsDto getStats() {
        long totalPatients = userRepository.count(); // simplified: includes admins
        long totalDoctors = doctorRepository.count();
        long totalAppointments = appointmentRepository.count();
        long appointmentsToday = appointmentRepository.countByDay(LocalDate.now());

        return new AdminStatsDto(totalPatients, totalDoctors, totalAppointments, appointmentsToday);
    }

    @Transactional(readOnly = true)
    public List<AdminAppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toAdminAppointmentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    private AdminAppointmentDto toAdminAppointmentDto(Appointment a) {
        String patientName = a.getPatient().getFullName();
        String doctorName = a.getDoctor().getName();
        return new AdminAppointmentDto(
                a.getId(),
                doctorName,
                patientName,
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}


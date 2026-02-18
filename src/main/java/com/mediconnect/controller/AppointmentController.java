package com.mediconnect.controller;

import com.mediconnect.dto.AppointmentRequestDto;
import com.mediconnect.dto.AppointmentResponseDto;
import com.mediconnect.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> book(@Valid @RequestBody AppointmentRequestDto request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(request));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public ResponseEntity<List<AppointmentResponseDto>> myAppointments() {
        return ResponseEntity.ok(appointmentService.getAppointmentsForCurrentPatient());
    }
}


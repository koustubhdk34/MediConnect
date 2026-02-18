package com.mediconnect.dto;

import com.mediconnect.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminAppointmentDto {
    private Long id;
    private String doctorName;
    private String patientName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
}


package com.mediconnect.dto;

import com.mediconnect.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppointmentResponseDto {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
}


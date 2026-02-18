package com.mediconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsDto {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long appointmentsToday;
}


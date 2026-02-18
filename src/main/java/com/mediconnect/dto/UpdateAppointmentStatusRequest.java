package com.mediconnect.dto;

import com.mediconnect.model.AppointmentStatus;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {
    private AppointmentStatus status;
}


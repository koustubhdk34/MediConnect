package com.mediconnect.controller;

import com.mediconnect.dto.AdminAppointmentDto;
import com.mediconnect.dto.AdminStatsDto;
import com.mediconnect.dto.UpdateAppointmentStatusRequest;
import com.mediconnect.model.AppointmentStatus;
import com.mediconnect.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appointments")
    public ResponseEntity<List<AdminAppointmentDto>> getAppointments() {
        return ResponseEntity.ok(adminService.getAllAppointments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/appointments/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestBody UpdateAppointmentStatusRequest request) {
        AppointmentStatus status = request.getStatus();
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
        adminService.updateAppointmentStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}


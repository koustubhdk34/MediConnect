package com.mediconnect.controller;

import com.mediconnect.dto.DoctorDto;
import com.mediconnect.model.Doctor;
import com.mediconnect.repository.DoctorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        return ResponseEntity.ok(new DoctorDto(saved.getId(), saved.getName(), saved.getSpecialization()));
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getDoctors() {
        List<DoctorDto> doctors = doctorRepository.findAll().stream()
                .map(d -> new DoctorDto(d.getId(), d.getName(), d.getSpecialization()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
}


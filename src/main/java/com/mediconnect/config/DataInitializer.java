package com.mediconnect.config;

import com.mediconnect.model.Doctor;
import com.mediconnect.repository.DoctorRepository;
import com.mediconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final DoctorRepository doctorRepository;

    @Override
    public void run(String... args) {
        // Ensure there is at least one ADMIN user
        userService.createAdminIfNotExists();

        if (doctorRepository.count() == 0) {
            doctorRepository.save(Doctor.builder()
                    .name("Dr. Alice Smith")
                    .specialization("Cardiology")
                    .build());
            doctorRepository.save(Doctor.builder()
                    .name("Dr. Bob Johnson")
                    .specialization("Dermatology")
                    .build());
        }
    }
}


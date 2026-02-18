package com.mediconnect.service;

import com.mediconnect.model.Role;
import com.mediconnect.model.User;
import com.mediconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User createAdminIfNotExists() {
        return userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User admin = User.builder()
                            .username("admin")
                            .fullName("Admin User")
                            .password(passwordEncoder.encode("admin123"))
                            .role(Role.ADMIN)
                            .build();
                    return userRepository.save(admin);
                });
    }

    public User registerPatient(String username, String fullName, String rawPassword) {
        User user = User.builder()
                .username(username)
                .fullName(fullName)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.PATIENT)
                .build();
        return userRepository.save(user);
    }
}


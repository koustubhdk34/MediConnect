package com.mediconnect.controller;

import com.mediconnect.dto.AuthDtos;
import com.mediconnect.model.User;
import com.mediconnect.security.JwtService;
import com.mediconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@RequestBody AuthDtos.LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authToken);

        User user = userService.getByUsername(request.getUsername());
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(new AuthDtos.LoginResponse(token, user.getRole().name(), user.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthDtos.RegisterRequest request) {
        userService.registerPatient(request.getUsername(), request.getFullName(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }
}


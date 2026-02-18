package com.mediconnect.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
public class AuthDtos {

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String fullName;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String role;
        private String username;
    }
}


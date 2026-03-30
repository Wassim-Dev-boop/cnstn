package com.alnaifer.nehb.dto;

import lombok.*;

/**
 * DTO pour la réponse d'authentification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo userInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
    }
}

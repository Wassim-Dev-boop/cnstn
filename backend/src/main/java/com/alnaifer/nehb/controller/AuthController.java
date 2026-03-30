package com.alnaifer.nehb.controller;

import com.alnaifer.nehb.dto.AuthResponse;
import com.alnaifer.nehb.dto.LoginRequest;
import com.alnaifer.nehb.model.User;
import com.alnaifer.nehb.security.JwtUtil;
import com.alnaifer.nehb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification
 * Gère la connexion, déconnexion et rafraîchissement de token
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Authentification utilisateur - Login
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Authentification
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Génération des tokens
        String accessToken = jwtUtil.generateToken(authentication.getPrincipal());
        String refreshToken = jwtUtil.generateRefreshToken(authentication.getPrincipal());

        // Récupération des infos utilisateur
        User user = userService.getUserByEmail(loginRequest.getEmail());

        // Construction de la réponse
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMs())
                .userInfo(userInfo)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Rafraîchissement du token d'accès
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        try {
            String email = jwtUtil.extractUsername(refreshToken);
            User user = userService.getUserByEmail(email);

            if (!jwtUtil.validateToken(refreshToken, user)) {
                throw new RuntimeException("Refresh token invalide ou expiré");
            }

            String newAccessToken = jwtUtil.generateToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole().name())
                    .build();

            AuthResponse response = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationMs())
                    .userInfo(userInfo)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Déconnexion - Invalidation du token (côté client)
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // La gestion des tokens JWT étant stateless, l'invalidation se fait côté client
        // Le client doit simplement supprimer le token de son stockage local
        return ResponseEntity.ok().build();
    }

    /**
     * Vérification du token - Endpoint pour valider un token
     * GET /api/v1/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userService.getUserByEmail(email);
            boolean isValid = jwtUtil.validateToken(token, user);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}

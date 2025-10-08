package com.careerit.isapp.auth.api;
import com.careerit.isapp.auth.domain.RefreshToken;
import com.careerit.isapp.auth.domain.Role;
import com.careerit.isapp.auth.domain.User;
import com.careerit.isapp.auth.dto.*;
import com.careerit.isapp.auth.service.RefreshTokenService;
import com.careerit.isapp.auth.service.UserDetailsServiceImpl;
import com.careerit.isapp.auth.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserDetailsServiceImpl userDetailsService;
        private final RefreshTokenService refreshTokenService;
        
        @Value("${jwt.expiration-minutes:60}")
        private Long jwtExpirationMinutes;

        @PostMapping("/register")
        public ResponseEntity<UserResponse> register(@RequestBody SignupRequest signupRequest) {
            User user = userDetailsService.signup(signupRequest);
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
            return ResponseEntity.ok(response);
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest authRequest){
            try {
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                UserDetails user = (UserDetails) auth.getPrincipal();
                User appUser = userDetailsService.loadAppUserByUsername(user.getUsername());
                
                // Generate access token
                String accessToken = jwtService.generateToken(user);
                
                // Generate refresh token
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
                
                // Build response
                Set<String> roles = appUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());
                
                TokenResponse response = TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .tokenType("Bearer")
                        .expiresIn(jwtExpirationMinutes * 60) // Convert minutes to seconds
                        .userId(appUser.getId())
                        .username(appUser.getUsername())
                        .email(appUser.getEmail())
                        .roles(roles)
                        .build();
                
                return ResponseEntity.ok(response);
            } catch (BadCredentialsException ex) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            } catch (AuthenticationException ex) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
            }
        }
        
        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
            String requestRefreshToken = request.getRefreshToken();
            
            try {
                return refreshTokenService.findByToken(requestRefreshToken)
                        .map(refreshTokenService::verifyExpiration)
                        .map(RefreshToken::getUser)
                        .map(user -> {
                            // Generate new access token
                            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                            String newAccessToken = jwtService.generateToken(userDetails);
                            
                            Set<String> roles = user.getRoles().stream()
                                    .map(Role::getName)
                                    .collect(Collectors.toSet());
                            
                            TokenResponse response = TokenResponse.builder()
                                    .accessToken(newAccessToken)
                                    .refreshToken(requestRefreshToken)
                                    .tokenType("Bearer")
                                    .expiresIn(jwtExpirationMinutes * 60)
                                    .userId(user.getId())
                                    .username(user.getUsername())
                                    .email(user.getEmail())
                                    .roles(roles)
                                    .build();
                            
                            return ResponseEntity.ok(response);
                        })
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));
            } catch (Exception e) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
            }
        }
        
        @PostMapping("/logout")
        public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
            try {
                refreshTokenService.revokeToken(request.getRefreshToken());
                return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("error", "Logout failed"));
            }
        }

}

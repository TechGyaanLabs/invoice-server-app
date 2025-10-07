package com.careerit.isapp.auth.api;
import com.careerit.isapp.auth.domain.User;
import com.careerit.isapp.auth.dto.LoginRequest;
import com.careerit.isapp.auth.dto.SignupRequest;
import com.careerit.isapp.auth.service.UserDetailsServiceImpl;
import com.careerit.isapp.auth.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        @Autowired
        private JwtService jwtService;

        @Autowired
        private  AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @PostMapping("/register")
        public ResponseEntity<User> register(@RequestBody SignupRequest signupRequest) {
            User user = userDetailsService.signup(signupRequest);
            return ResponseEntity.ok(user);
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest authRequest){

            try {
                var authToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
                Authentication auth = authenticationManager.authenticate(authToken);
                UserDetails user = (UserDetails) auth.getPrincipal();
                String token = jwtService.generateToken(user);
                return ResponseEntity.ok(Map.of("jwt",token));
            } catch (BadCredentialsException ex) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            } catch (AuthenticationException ex) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
            }

        }

}

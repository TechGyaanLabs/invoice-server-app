package com.careerit.isapp.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.careerit.isapp.appconfig.AuthRequest;
import com.careerit.isapp.appconfig.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        @Autowired
        private JwtService jwtService;

        @Autowired
        private  AuthenticationManager authenticationManager;

        @PostMapping("/register")
        public ResponseEntity<String> register(){
            return ResponseEntity.ok("OK");
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequest authRequest){

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

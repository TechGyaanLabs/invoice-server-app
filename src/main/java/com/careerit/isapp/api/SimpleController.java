package com.careerit.isapp.api;

import com.careerit.isapp.auth.util.SecurityContextUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
public class SimpleController {

        @GetMapping("/greet")
        @PreAuthorize("hasAnyRole('ADMIN','USER')")
        public ResponseEntity<Map<String,String>> greetings() {
            String username = SecurityContextUserDetails.username();
            Map<String,String> map = new HashMap<>();
            map.put("message", String.format("Hello %s, Welcome to Spring Boot world!", username));
            return ResponseEntity.ok(map);
        }

        @GetMapping("/sysinfo")
        @PreAuthorize("hasAnyRole('ADMIN')")
        public ResponseEntity<Map<String,String>> systemInfo() {
            Map<String,String> map = new HashMap<>();
            map.put("os.name", System.getProperty("os.name"));
            return ResponseEntity.ok(map);
        }

}

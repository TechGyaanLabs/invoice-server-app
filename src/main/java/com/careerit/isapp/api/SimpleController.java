package com.careerit.isapp.api;

import com.careerit.isapp.auth.util.SecurityContextUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
public class SimpleController {

        @GetMapping("/greet")
        public ResponseEntity<Map<String,String>> greetings() {
            String username = SecurityContextUserDetails.username();
            Map<String,String> map = new HashMap<>();
            map.put("message", String.format("Hello %s, Welcome to Spring Boot world!", username));
            return ResponseEntity.ok(map);
        }

}

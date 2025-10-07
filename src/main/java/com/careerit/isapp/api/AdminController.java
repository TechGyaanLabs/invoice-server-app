package com.careerit.isapp.api;

import com.careerit.isapp.auth.util.SecurityContextUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/greet")
    public ResponseEntity<Map<String,String>> getAdminWithMessage(){
        String username = SecurityContextUserDetails.username();
        return ResponseEntity.ok().body(Map.of("username",username,"message","Welcome to spring boot world"));
    }
}

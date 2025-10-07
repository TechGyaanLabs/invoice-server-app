package com.careerit.isapp.api;

import com.careerit.isapp.auth.util.SecurityContextUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @GetMapping("/greet")
    public ResponseEntity<Map<String,String>> getUserWithMessage(){
        String username = SecurityContextUserDetails.username();
        return ResponseEntity.ok().body(Map.of("username",username,"message","Welcome to spring boot world"));
    }
}

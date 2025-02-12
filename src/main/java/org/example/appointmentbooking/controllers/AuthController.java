package org.example.appointmentbooking.controllers;

import org.example.appointmentbooking.dto.AuthRequest;
import org.example.appointmentbooking.dto.AuthResponse;
import org.example.appointmentbooking.models.User;
import org.example.appointmentbooking.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 📌 User Signup
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return authService.registerUser(user);
    }

    // 📌 User Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        return authService.loginUser(request);
    }
}

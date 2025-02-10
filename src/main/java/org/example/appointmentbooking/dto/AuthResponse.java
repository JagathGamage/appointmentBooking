package org.example.appointmentbooking.dto;

import lombok.*;
import org.example.appointmentbooking.models.Role;

@Getter
@Builder // 🟢 Enables flexible object creation
@AllArgsConstructor
@ToString(exclude = "token") // 🚨 Avoids exposing sensitive data in logs
public class AuthResponse {
    private final String token;
    private final String email;
    private final Role role;
}

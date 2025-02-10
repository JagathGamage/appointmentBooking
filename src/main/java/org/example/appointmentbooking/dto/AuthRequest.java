package org.example.appointmentbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor  // 🟢 Helps in serialization/deserialization
public class AuthRequest {

    @Email(message = "Invalid email format")  // 🟢 Ensures a valid email format
    @NotBlank(message = "Email is required") // 🛑 Prevents empty values
    private String email;

    @NotBlank(message = "Password is required") // 🛑 Ensures password is provided
    private String password;
}

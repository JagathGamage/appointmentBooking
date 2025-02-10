package org.example.appointmentbooking.services;

import org.example.appointmentbooking.dto.SignupRequest;
import org.example.appointmentbooking.models.Role;
import org.example.appointmentbooking.models.User;
import org.example.appointmentbooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use!");  // 🔴 Use exception instead of returning a string
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf("USER"));  // ✅ Assign default role

        userRepository.save(user);
        return "User registered successfully!";
    }
}

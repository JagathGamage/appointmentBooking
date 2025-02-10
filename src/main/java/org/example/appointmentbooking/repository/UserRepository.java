package org.example.appointmentbooking.repository;

import org.example.appointmentbooking.models.Role;
import org.example.appointmentbooking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Find user by email

    boolean existsByEmail(String email);  // ✅ Check if an email is already registered

    Optional<User> findByEmailAndRole(String email, Role role); // ✅ Find user with specific role (optional)
}

package org.example.appointmentbooking.controllers;

import org.example.appointmentbooking.dto.AppointmentRequest;
import org.example.appointmentbooking.dto.AppointmentResponse;
import org.example.appointmentbooking.dto.UserResponse;
import org.example.appointmentbooking.models.Appointment;
import org.example.appointmentbooking.models.Role;
import org.example.appointmentbooking.models.User;
import org.example.appointmentbooking.repository.AppointmentRepository;
import org.example.appointmentbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend requests
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // 📌 Book an appointment (User)
    @PostMapping("/book")
    @Transactional
    public ResponseEntity<String> bookAppointment(@RequestBody AppointmentRequest request) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(request.getAppointmentId());

        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.badRequest().body("Appointment not found!");
        }

        Appointment appointment = optionalAppointment.get();

        if (appointment.getScheduled()) {
            return ResponseEntity.badRequest().body("Appointment is already booked!");
        }

        // 🔹 Find or create user
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setRole(Role.USER); // Default role
            user = userRepository.save(user);
        }

        appointment.setUser(user);
        appointment.setScheduled(true);
        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment booked successfully!");
    }

    // 📌 Fetch all available (unbooked) appointments
    @GetMapping("/available")
    public ResponseEntity<List<Appointment>> getAvailableAppointments() {
        return ResponseEntity.ok(appointmentRepository.findByScheduledFalse());
    }

    // 📌 Get all appointments booked by a specific user
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getAppointmentsByUserEmail(@PathVariable String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!");
        }
        return ResponseEntity.ok(appointmentRepository.findByUser(optionalUser.get()));
    }

    // 📌 Cancel a booked appointment (User)
    @PreAuthorize("hasAuthority('USER')") // Corrected authority check
    @Transactional
    @PostMapping("/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.badRequest().body("Appointment not found!");
        }

        Appointment appointment = optionalAppointment.get();

        if (!appointment.getScheduled() || appointment.getUser() == null) {
            return ResponseEntity.badRequest().body("Appointment is not booked!");
        }

        appointment.setUser(null);
        appointment.setScheduled(false);
        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment canceled successfully!");
    }

    @GetMapping("/getappointment/{id}")
    public ResponseEntity<Object> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isPresent()) {
            return ResponseEntity.ok(appointment.get()); // Returns the Appointment object
        } else {
            return ResponseEntity.status(404).body("Appointment not found"); // Returns a String message
        }
    }


    // 🛠️ ADMIN ENDPOINTS 🛠️

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> responseList = appointmentRepository.findAll().stream().map(appointment -> {
            return new AppointmentResponse(
                    appointment.getId(),
                    new int[]{appointment.getDate().getYear(), appointment.getDate().getMonthValue(), appointment.getDate().getDayOfMonth()},
                    new int[]{appointment.getStartTime().getHour(), appointment.getStartTime().getMinute()},
                    new int[]{appointment.getEndTime().getHour(), appointment.getEndTime().getMinute()},
                    appointment.getScheduled(),
                    (appointment.getUser() != null) ? new UserResponse(
                            appointment.getUser().getName(),
                            appointment.getUser().getEmail()
                    ) : null
            );
        }).toList();

        return ResponseEntity.ok(responseList);
    }



    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addAppointment(@RequestBody Appointment appointment) {
        LocalDate date = appointment.getDate();
        LocalTime newStartTime = appointment.getStartTime();
        LocalTime newEndTime = appointment.getEndTime();

        // Validate that end time is after start time
        if (newEndTime.isBefore(newStartTime) || newEndTime.equals(newStartTime)) {
            return ResponseEntity.badRequest().body("End time must be after start time.");
        }

        // Fetch all existing appointments for the given date
        List<Appointment> existingAppointments = appointmentRepository.findByDate(date);

        // Check for overlapping time slots
        boolean conflictExists = existingAppointments.stream().anyMatch(existingAppointment -> {
            LocalTime existingStartTime = existingAppointment.getStartTime();
            LocalTime existingEndTime = existingAppointment.getEndTime();

            // Condition for time slot overlap

            return !(newEndTime.isBefore(existingStartTime) || newStartTime.isAfter(existingEndTime));
        });

        if (conflictExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An appointment already exists within this time range.");
        }

        // Save the appointment
        appointment.setScheduled(false);
        appointmentRepository.save(appointment);
        return ResponseEntity.ok("Appointment added successfully!");
    }




    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<String> updateAppointment(@PathVariable Long id, @RequestBody Appointment updatedAppointment) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    if (appointment.getScheduled()) {
                        return ResponseEntity.badRequest().body("Cannot edit a booked appointment!");
                    }
                    appointment.setDate(updatedAppointment.getDate());
                    appointment.setStartTime(updatedAppointment.getStartTime());
                    appointment.setEndTime(updatedAppointment.getEndTime());
                    appointmentRepository.save(appointment);
                    return ResponseEntity.ok("Appointment updated successfully!");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Appointment not found!"));
    }


    // 📌 Admin: Delete an appointment
    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Appointment not found!");
        }
        appointmentRepository.deleteById(id);
        return ResponseEntity.ok("Appointment deleted successfully!");
    }
}

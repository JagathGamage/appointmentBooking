package org.example.appointmentbooking.services;


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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // 📌 Book an appointment
    @Transactional
    public ResponseEntity<String> bookAppointment(AppointmentRequest request) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(request.getAppointmentId());

        if (optionalAppointment.isEmpty()) {
            return ResponseEntity.badRequest().body("Appointment not found!");
        }

        Appointment appointment = optionalAppointment.get();

        if (appointment.getScheduled()) {
            return ResponseEntity.badRequest().body("Appointment is already booked!");
        }

        // Find or create user
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setRole(Role.USER);
            user = userRepository.save(user);
        }

        appointment.setUser(user);
        appointment.setScheduled(true);
        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment booked successfully!");
    }

    // 📌 Get all available appointments
    public List<Appointment> getAvailableAppointments() {
        return appointmentRepository.findByScheduledFalse();
    }

    // 📌 Get all appointments by user email
    public ResponseEntity<?> getAppointmentsByUserEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!");
        }
        return ResponseEntity.ok(appointmentRepository.findByUser(optionalUser.get()));
    }

    // 📌 Cancel an appointment
    @Transactional
    public ResponseEntity<String> cancelAppointment(Long appointmentId) {
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

    // 📌 Get appointment by ID
    public ResponseEntity<Object> getAppointmentById(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        return appointment.isPresent()
                ? ResponseEntity.ok(appointment.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
    }

    // 📌 Get all appointments (Admin)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream().map(appointment ->
                new AppointmentResponse(
                        appointment.getId(),
                        new int[]{appointment.getDate().getYear(), appointment.getDate().getMonthValue(), appointment.getDate().getDayOfMonth()},
                        new int[]{appointment.getStartTime().getHour(), appointment.getStartTime().getMinute()},
                        new int[]{appointment.getEndTime().getHour(), appointment.getEndTime().getMinute()},
                        appointment.getScheduled(),
                        (appointment.getUser() != null) ? new UserResponse(
                                appointment.getUser().getName(),
                                appointment.getUser().getEmail()
                        ) : null
                )
        ).toList();
    }

    // 📌 Add an appointment (Admin)
    @Transactional
    public ResponseEntity<String> addAppointment(Appointment appointment) {
        LocalDate date = appointment.getDate();
        LocalTime newStartTime = appointment.getStartTime();
        LocalTime newEndTime = appointment.getEndTime();

        if (newEndTime.isBefore(newStartTime) || newEndTime.equals(newStartTime)) {
            return ResponseEntity.badRequest().body("End time must be after start time.");
        }

        List<Appointment> existingAppointments = appointmentRepository.findByDate(date);

        boolean conflictExists = existingAppointments.stream().anyMatch(existingAppointment ->
                !(newEndTime.isBefore(existingAppointment.getStartTime()) || newStartTime.isAfter(existingAppointment.getEndTime()))
        );

        if (conflictExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An appointment already exists within this time range.");
        }

        appointment.setScheduled(false);
        appointmentRepository.save(appointment);
        return ResponseEntity.ok("Appointment added successfully!");
    }

    // 📌 Update an appointment (Admin)
    @Transactional
    public ResponseEntity<String> updateAppointment(Long id, Appointment updatedAppointment) {
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

    // 📌 Delete an appointment (Admin)
    public ResponseEntity<String> deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Appointment not found!");
        }
        appointmentRepository.deleteById(id);
        return ResponseEntity.ok("Appointment deleted successfully!");
    }
}

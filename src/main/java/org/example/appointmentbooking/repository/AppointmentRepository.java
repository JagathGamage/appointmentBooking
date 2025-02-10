package org.example.appointmentbooking.repository;

import org.example.appointmentbooking.models.Appointment;
import org.example.appointmentbooking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // ✅ Fix: Use user.email instead of non-existent userEmail field
    List<Appointment> findByUser_Email(String email);

    List<Appointment> findByScheduledFalse();

    List<Appointment> findByUser(User user);

//    boolean existsByDateAndTime(LocalDate date, LocalTime time);

    boolean existsByDateAndStartTime(LocalDate date, LocalTime startTime);

    List<Appointment> findByDate(LocalDate date);
}

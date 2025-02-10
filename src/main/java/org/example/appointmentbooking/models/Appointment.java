package org.example.appointmentbooking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;  // Stores the date

    @Column(nullable = false)
    private LocalTime startTime;  // Stores the time

    @Column(nullable = false)
    private LocalTime endTime;  // Stores the time

    @Column(nullable = false)
    private Boolean scheduled;

    @ManyToOne(optional = true) // ✅ Appointments can exist without a user
    @JoinColumn(name = "user_id", nullable = true) // ✅ Foreign key can be NULL
    @JsonIgnore // ✅ Prevents infinite recursion when serializing JSON responses
    private User user;
}

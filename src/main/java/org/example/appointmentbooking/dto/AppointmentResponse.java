package org.example.appointmentbooking.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private int[] date;
    private int[] startTime;
    private int[] endTime;
    private boolean scheduled;
    private UserResponse user;
}

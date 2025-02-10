package org.example.appointmentbooking.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentRequest {
    private Long appointmentId;
    private String name;
    private String email;
}

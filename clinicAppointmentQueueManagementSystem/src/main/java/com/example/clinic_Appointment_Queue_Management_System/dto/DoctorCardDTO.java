package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorCardDTO {
    private String doctorId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String specializationId;
    private String specializationName;
    private String licenseNumber;
    private String phoneNumber;
    private String email;
    private List<DoctorAvailabilityDTO> availableSlots;
    private Double consultationFee;
    private String currency;
    private Double averageRating;
    private Long totalRatings;
}

package com.example.clinic_Appointment_Queue_Management_System.dto;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    private String doctorId;
    private String userId;
    private String firstName;
    private String lastName;
    private String specializationId;
    private String specializationName;
    private String licenseNumber;
    private String phoneNumber;
    private String email;
    private Status status;
}

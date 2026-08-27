package com.example.clinic_Appointment_Queue_Management_System.dto;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private String patientId;
    private String userId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String contact;
    private String address;
    private String emergencyContact;
    private Status status;
}

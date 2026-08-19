package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    private String patientId;

    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String contact;
    private String address;
    private String emergencyContact;

    @Enumerated(EnumType.STRING)
    private Status status;
}

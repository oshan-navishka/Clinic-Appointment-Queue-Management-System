package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Doctor {
    @Id
    private String doctorId;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User  user;

    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status;
}

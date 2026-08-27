package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    private String userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Doctor doctor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Patient patient;

}

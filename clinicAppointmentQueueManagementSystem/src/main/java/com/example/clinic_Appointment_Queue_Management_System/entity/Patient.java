package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Gender;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Patient {
    @Id
    private String patientId;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    private String firstName;

    private String lastName;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String contact;

    private String address;

    private String emergencyContact;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointments> appointments;
}

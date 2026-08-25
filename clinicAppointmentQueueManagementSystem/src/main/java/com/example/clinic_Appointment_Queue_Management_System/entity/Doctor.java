package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<DoctorSchedules> doctorSchedules;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointments> appointments;
}

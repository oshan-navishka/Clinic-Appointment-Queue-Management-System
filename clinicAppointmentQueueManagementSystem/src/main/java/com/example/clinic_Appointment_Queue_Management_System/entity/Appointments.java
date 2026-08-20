package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointments {
    @Id
    private String appointmentId;

    @ManyToOne
    @JoinColumn(name = "patientId", nullable = false)
    private Patient patient;

    private String doctorId;
    private String branchId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int appointmentNumber;
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;


}

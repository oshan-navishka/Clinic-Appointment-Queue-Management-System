package com.example.clinic_Appointment_Queue_Management_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class Prescription {
    @Id
    private String prescriptionId;

    @ManyToOne
    @JoinColumn(name = "appointmentId", nullable = false)
    private Appointments appointment;

    @ManyToOne
    @JoinColumn(name = "patientId", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctorId", nullable = false)
    private Doctor doctor;

    private LocalDate issuedDate;

    @Column(length = 3000)
    private String medicineDetails;

    @Column(length = 1000)
    private String instructions;

    private LocalDate validUntil;
}

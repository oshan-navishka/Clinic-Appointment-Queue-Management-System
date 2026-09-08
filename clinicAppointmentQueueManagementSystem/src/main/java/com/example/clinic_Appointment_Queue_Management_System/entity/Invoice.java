package com.example.clinic_Appointment_Queue_Management_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class Invoice {
    @Id
    private String invoiceId;

    @OneToOne
    @JoinColumn(name = "appointmentId", unique = true)
    private Appointments appointment;

    @ManyToOne
    @JoinColumn(name = "patientId", nullable = false)
    private Patient patient;

    private Double amount;
    private String currency;
    private String paymentMethod;
    private String invoiceStatus;
    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
}

package com.example.clinic_Appointment_Queue_Management_System.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class AppointmentFee {
    @Id
    private String feeId;

    @OneToOne
    @JoinColumn(name = "doctorId", unique = true)
    private Doctor doctor;

    private Double consultationFee;
    private String currency;
    private String notes;
}

package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.AppointmentState;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.BookingSource;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.PaymentStatus;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointments {
    @Id
    private String appointmentId;

    @ManyToOne
    @JoinColumn(name = "patientId", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctorId", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "branchId")
    private ClinicBranches clinicBranches;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int appointmentNumber;
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private BookingSource bookingSource;

    @Enumerated(EnumType.STRING)
    private AppointmentState appointmentState;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Double paymentAmount;

    private LocalDateTime paidAt;


}

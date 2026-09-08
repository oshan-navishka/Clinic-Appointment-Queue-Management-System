package com.example.clinic_Appointment_Queue_Management_System.dto;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.AppointmentState;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.BookingSource;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.PaymentStatus;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String userId;
    private String branchId;
    private String patientName;
    private String doctorName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Integer appointmentNumber;
    private String reason;
    private Status status;
    private BookingSource bookingSource;
    private AppointmentState appointmentState;
    private PaymentStatus paymentStatus;
    private Double paymentAmount;
    private LocalDateTime paidAt;
    private String doctorSpecialization;
    private String patientContact;
}

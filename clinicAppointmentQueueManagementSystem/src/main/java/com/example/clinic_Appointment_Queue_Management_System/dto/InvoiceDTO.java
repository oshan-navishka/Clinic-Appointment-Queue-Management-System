package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class InvoiceDTO {
    private String invoiceId;
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorName;
    private String specializationName;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String invoiceStatus;
    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
}

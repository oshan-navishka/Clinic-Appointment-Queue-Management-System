package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDTO {
    private String recordId;
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String patientContact;
    private Integer patientAge;
    private String patientGender;
    private String doctorId;
    private String doctorName;
    private String specializationName;
    private LocalDate visitDate;
    private String diagnosis;
    private String treatment;
    private String notes;
}

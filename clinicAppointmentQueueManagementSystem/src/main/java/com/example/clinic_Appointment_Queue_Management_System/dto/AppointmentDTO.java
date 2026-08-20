package com.example.clinic_Appointment_Queue_Management_System.dto;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String branchId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int appointmentNumber;
    private String reason;
    private Status status;
}

package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminOverviewDTO {
    private Long doctorCount;
    private Long patientCount;
    private Long pendingCount;
    private Long checkedCount;
    private Long unpaidCount;
    private List<DoctorDTO> doctors;
    private List<PatientDTO> patients;
    private List<AppointmentDTO> pendingAppointments;
    private List<AppointmentDTO> checkedAppointments;
    private List<AppointmentDTO> allAppointments;
    private List<DoctorPatientsDTO> doctorPatients;
}

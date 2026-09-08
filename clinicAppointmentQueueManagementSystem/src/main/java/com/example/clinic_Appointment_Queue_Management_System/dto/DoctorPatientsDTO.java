package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorPatientsDTO {
    private String doctorId;
    private String doctorName;
    private String specializationName;
    private List<AppointmentDTO> appointments;
    private List<AppointmentDTO> pendingAppointments;
    private List<AppointmentDTO> checkedAppointments;
    private Long pendingCount;
    private Long checkedCount;
}

package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDashboardDTO {
    private String doctorId;
    private String doctorName;
    private String specializationName;
    private List<AppointmentDTO> thisWeekAppointments;
    private List<AppointmentDTO> pendingAppointments;
    private List<AppointmentDTO> checkedAppointments;
    private List<AppointmentDTO> allAppointments;
    private Long totalCount;
    private Long pendingCount;
    private Long checkedCount;
    private Long thisWeekCount;
}

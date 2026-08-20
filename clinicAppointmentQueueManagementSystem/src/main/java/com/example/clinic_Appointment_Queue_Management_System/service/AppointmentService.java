package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;

public interface AppointmentService {
//    void addAppointment(String patientName, String doctorName, String appointmentTime);

    void addAppointment(AppointmentDTO appointmentDTO);
}

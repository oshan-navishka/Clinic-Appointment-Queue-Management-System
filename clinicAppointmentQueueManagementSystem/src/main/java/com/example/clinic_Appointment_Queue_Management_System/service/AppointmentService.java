package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.AdminOverviewDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDashboardDTO;

import java.util.List;

public interface AppointmentService {

    void addAppointment(AppointmentDTO appointmentDTO);

    void bookOnline(AppointmentDTO appointmentDTO);

    void payAppointment(String appointmentId, String userId);

    void adminConfirmPayment(String appointmentId);

    void markChecked(String appointmentId, String userId);

    List<AppointmentDTO> getAllAppointments();

    List<AppointmentDTO> getDoctorWeekAppointments(String userId);

    List<AppointmentDTO> getDoctorAllAppointments(String userId);

    List<AppointmentDTO> getMyAppointments(String userId);

    DoctorDashboardDTO getDoctorDashboard(String userId);

    AdminOverviewDTO getAdminOverview();
}

package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;

public interface EmailService {
    void sendBookingConfirmation(String toEmail, String patientName, AppointmentDTO appointment);
    void sendPaymentConfirmation(String toEmail, String patientName, AppointmentDTO appointment);
    void sendWelcomeEmail(String toEmail, String name, String username, String password, String role);
}

package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    void createNotification(String userId, String title, String message, String type, String referenceId);
    List<NotificationDTO> getUserNotifications(String userId);
    long getUnreadCount(String userId);
    void markAllRead(String userId);
}

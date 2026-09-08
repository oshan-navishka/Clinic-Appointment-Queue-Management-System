package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class NotificationDTO {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private boolean isRead;
    private String type;
    private LocalDateTime createdAt;
    private String referenceId;
}

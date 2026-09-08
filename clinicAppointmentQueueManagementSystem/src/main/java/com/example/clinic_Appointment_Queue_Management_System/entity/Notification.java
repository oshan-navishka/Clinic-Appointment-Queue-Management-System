package com.example.clinic_Appointment_Queue_Management_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class Notification {
    @Id
    private String notificationId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String title;

    @Column(length = 1000)
    private String message;

    private boolean isRead;
    private String type;
    private LocalDateTime createdAt;
    private String referenceId;
}

package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCredentialsDTO {
    private String userId;
    private String currentPassword;
    private String newUsername;
    private String newPassword;
}

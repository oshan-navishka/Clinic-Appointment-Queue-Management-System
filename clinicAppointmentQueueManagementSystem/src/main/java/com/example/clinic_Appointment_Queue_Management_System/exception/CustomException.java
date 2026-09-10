package com.example.clinic_Appointment_Queue_Management_System.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private int status;
    private String message;
}

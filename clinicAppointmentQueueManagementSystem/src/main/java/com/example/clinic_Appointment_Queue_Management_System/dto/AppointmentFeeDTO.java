package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AppointmentFeeDTO {
    private String feeId;
    private String doctorId;
    private Double consultationFee;
    private String currency;
    private String notes;
}

package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationsDTO {
    private String specializationId;
    private String name;
    private String description;
    private String status;
}

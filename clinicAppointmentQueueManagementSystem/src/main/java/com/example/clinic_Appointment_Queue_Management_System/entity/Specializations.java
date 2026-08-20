package com.example.clinic_Appointment_Queue_Management_System.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Specializations {
    @Id
    private String specializationId;
    private String name;
    private String description;
    private String status;
}

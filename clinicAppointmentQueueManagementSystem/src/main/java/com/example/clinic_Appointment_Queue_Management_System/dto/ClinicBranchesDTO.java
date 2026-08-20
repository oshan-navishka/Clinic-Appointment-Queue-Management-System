package com.example.clinic_Appointment_Queue_Management_System.dto;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicBranchesDTO {
    private String branchId;
    private String branchName;
    private String address;
    private String phone;
    private String email;
    private Status status;
}

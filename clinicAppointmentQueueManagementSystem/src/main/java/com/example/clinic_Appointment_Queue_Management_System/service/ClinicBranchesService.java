package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.ClinicBranchesDTO;

import java.util.List;

public interface ClinicBranchesService {
    void saveClinicBranches(ClinicBranchesDTO clinicBranchesDTO);
    List<ClinicBranchesDTO> getAllBranches();
    void updateBranch(ClinicBranchesDTO clinicBranchesDTO);
    void deleteBranch(String branchId);
}

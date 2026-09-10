package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientMedicationDTO;

import java.util.List;

public interface PatientMedicationService {
    void saveMedication(PatientMedicationDTO dto);
    List<PatientMedicationDTO> getByPatientId(String patientId);
    List<PatientMedicationDTO> getByUserId(String userId);
    List<PatientMedicationDTO> getApprovedByUserId(String userId);
    List<PatientMedicationDTO> getPendingApproval();
    void approveMedication(String medicationId);
    void updateStatus(String medicationId, String status);
    void deleteMedication(String medicationId);
}

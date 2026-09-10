package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.MedicalRecordDTO;

import java.util.List;

public interface MedicalRecordService {
    void saveRecord(MedicalRecordDTO dto);
    List<MedicalRecordDTO> getByPatientId(String patientId);
    List<MedicalRecordDTO> getByUserId(String userId);
    MedicalRecordDTO getByAppointmentId(String appointmentId);
}

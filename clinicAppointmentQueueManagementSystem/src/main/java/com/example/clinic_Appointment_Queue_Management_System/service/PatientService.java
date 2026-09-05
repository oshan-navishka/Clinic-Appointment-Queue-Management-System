package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientDTO;

import java.util.List;

public interface PatientService {
    void savePatient(PatientDTO patientDTO);

    List<PatientDTO> getAllPatients();

    void updatePatient(PatientDTO patientDTO);
}

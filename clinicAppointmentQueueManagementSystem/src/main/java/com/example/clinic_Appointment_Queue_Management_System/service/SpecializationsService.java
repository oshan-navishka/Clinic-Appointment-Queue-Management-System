package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.SpecializationsDTO;

import java.util.List;

public interface SpecializationsService {
    void saveSpecialization(SpecializationsDTO specializationsDTO);

    List<SpecializationsDTO> getAllSpecializations();

    void updateSpecialization(SpecializationsDTO specializationsDTO);

    void deleteSpecialization(String specializationId);
}

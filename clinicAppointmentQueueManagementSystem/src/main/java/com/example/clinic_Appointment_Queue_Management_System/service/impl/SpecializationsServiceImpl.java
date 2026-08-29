package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.SpecializationsDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Specializations;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.SpecializationsRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.SpecializationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpecializationsServiceImpl implements SpecializationsService {
    private final SpecializationsRepository specializationsRepository;

    @Override
    public void saveSpecialization(SpecializationsDTO specializationsDTO) {
        log.info("In SpecializationsImpl saveSpecialization");
        try{
            long count = specializationsRepository.count();
            String generatedId = String.format("S%03d", count + 1);

            Specializations specializations = new Specializations();
            specializations.setSpecializationId(generatedId);
            specializations.setName(specializationsDTO.getName());
            specializations.setDescription(specializationsDTO.getDescription());
            specializations.setStatus(Status.ACTIVE);
            specializationsRepository.save(specializations);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.SpecializationsDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Specializations;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.SpecializationsRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.SpecializationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<SpecializationsDTO> getAllSpecializations() {
        log.info("In SpecializationsImpl getAllSpecializations");
        try {
            List<SpecializationsDTO> specializationsDTOS = new ArrayList<>();
            List<Specializations> specializationsList = specializationsRepository.findByStatus(Status.ACTIVE);
            for (Specializations specializations : specializationsList) {
                SpecializationsDTO specializationsDTO = new SpecializationsDTO();
                specializationsDTO.setSpecializationId(specializations.getSpecializationId());
                specializationsDTO.setName(specializations.getName());
                specializationsDTO.setDescription(specializations.getDescription());
                specializationsDTO.setStatus(specializations.getStatus());
                specializationsDTOS.add(specializationsDTO);
            }
            return specializationsDTOS;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSpecialization(SpecializationsDTO specializationsDTO) {
        log.info("In SpecializationsImpl updateSpecialization");
        try{
            Optional<Specializations> optionalSpecialization = specializationsRepository.findById(specializationsDTO.getSpecializationId());

            if (optionalSpecialization.isEmpty())
                throw new RuntimeException("Specialization not found with ID: " + specializationsDTO.getSpecializationId());
            Specializations specializations = optionalSpecialization.get();
            specializations.setName(specializationsDTO.getName());
            specializations.setDescription(specializationsDTO.getDescription());
            specializationsRepository.save(specializations);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpecialization(String specializationId) {
        log.info("In SpecializationsImpl deleteSpecialization");
        try{
            Optional<Specializations> optionalSpecialization = specializationsRepository.findById(specializationId);

            if (optionalSpecialization.isEmpty())
                throw new RuntimeException("Specialization not found with ID: " + specializationId);
            Specializations specializations = optionalSpecialization.get();
            specializations.setStatus(Status.INACTIVE);
            specializationsRepository.save(specializations);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

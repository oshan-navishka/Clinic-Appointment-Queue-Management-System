package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.SpecializationsDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Specializations;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
import com.example.clinic_Appointment_Queue_Management_System.repository.SpecializationsRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.SpecializationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpecializationsServiceImpl implements SpecializationsService {

    private final SpecializationsRepository specializationsRepository;

    @Override
    public void saveSpecialization(SpecializationsDTO dto) {
        log.info("Saving specialization: {}", dto.getName());
        try {
            long count = specializationsRepository.count();
            String generatedId = String.format("S%03d", count + 1);

            Specializations s = new Specializations();
            s.setSpecializationId(generatedId);
            s.setName(dto.getName());
            s.setDescription(dto.getDescription());
            s.setStatus(Status.ACTIVE);
            specializationsRepository.save(s);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving specialization", e);
            throw new CustomException(500, "Error occurred while saving specialization");
        }
    }

    @Override
    public List<SpecializationsDTO> getAllSpecializations() {
        log.info("Fetching all specializations");
        try {
            List<SpecializationsDTO> list = new ArrayList<>();
            for (Specializations s : specializationsRepository.findByStatus(Status.ACTIVE)) {
                SpecializationsDTO dto = new SpecializationsDTO();
                dto.setSpecializationId(s.getSpecializationId());
                dto.setName(s.getName());
                dto.setDescription(s.getDescription());
                dto.setStatus(s.getStatus());
                list.add(dto);
            }
            return list;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching specializations", e);
            throw new CustomException(500, "Error occurred while fetching specializations");
        }
    }

    @Override
    public void updateSpecialization(SpecializationsDTO dto) {
        log.info("Updating specialization: {}", dto.getSpecializationId());
        try {
            Specializations s = specializationsRepository.findById(dto.getSpecializationId())
                    .orElseThrow(() -> new CustomException(404, "Specialization not found"));
            s.setName(dto.getName());
            s.setDescription(dto.getDescription());
            specializationsRepository.save(s);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating specialization", e);
            throw new CustomException(500, "Error occurred while updating specialization");
        }
    }

    @Override
    public void deleteSpecialization(String specializationId) {
        log.info("Deleting specialization: {}", specializationId);
        try {
            Specializations s = specializationsRepository.findById(specializationId)
                    .orElseThrow(() -> new CustomException(404, "Specialization not found"));
            s.setStatus(Status.INACTIVE);
            specializationsRepository.save(s);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting specialization", e);
            throw new CustomException(500, "Error occurred while deleting specialization");
        }
    }
}

package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.ClinicBranchesDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.ClinicBranches;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.ClinicBranchesRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.ClinicBranchesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicBranchesServiceImpl implements ClinicBranchesService {
    private ClinicBranchesRepository clinicBranchesRepository;


    @Override
    public void saveClinicBranches(ClinicBranchesDTO clinicBranchesDTO) {
        log.info("ClinicBranchesServiceImpl.saveClinicBranches()");
        try {
            long count = clinicBranchesRepository.count();
            String generatedId = String.format("CB%03d", count + 1);

            ClinicBranches clinicBranches = new ClinicBranches();
            clinicBranches.setBranchId(generatedId);
            clinicBranches.setBranchName(clinicBranchesDTO.getBranchName());
            clinicBranches.setAddress(clinicBranchesDTO.getAddress());
            clinicBranches.setPhone(clinicBranchesDTO.getPhone());
            clinicBranches.setEmail(clinicBranchesDTO.getEmail());
            clinicBranches.setStatus(Status.ACTIVE);
        }catch (Exception e){
            log.error("Error saving clinic branch {}", clinicBranchesDTO);
            throw e;
        }
    }
}

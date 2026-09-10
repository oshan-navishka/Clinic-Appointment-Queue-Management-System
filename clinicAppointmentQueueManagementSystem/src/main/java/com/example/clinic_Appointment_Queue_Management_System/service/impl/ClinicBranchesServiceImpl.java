package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.ClinicBranchesDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.ClinicBranches;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.ClinicBranchesRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.ClinicBranchesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClinicBranchesServiceImpl implements ClinicBranchesService {

    private final ClinicBranchesRepository branchRepository;

    @Override
    @Transactional
    public void saveClinicBranches(ClinicBranchesDTO dto) {
        log.info("Saving clinic branch: {}", dto.getBranchName());

        long count = branchRepository.count();
        String id  = String.format("BR%03d", count + 1);

        ClinicBranches branch = new ClinicBranches();
        branch.setBranchId(id);
        branch.setBranchName(dto.getBranchName());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());
        branch.setEmail(dto.getEmail());
        branch.setStatus(Status.ACTIVE);
        branchRepository.save(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicBranchesDTO> getAllBranches() {
        return branchRepository.findAll()
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateBranch(ClinicBranchesDTO dto) {
        ClinicBranches branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found: " + dto.getBranchId()));
        branch.setBranchName(dto.getBranchName());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());
        branch.setEmail(dto.getEmail());
        if (dto.getStatus() != null) branch.setStatus(dto.getStatus());
        branchRepository.save(branch);
    }

    @Override
    @Transactional
    public void deleteBranch(String branchId) {
        ClinicBranches branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));
        branch.setStatus(Status.INACTIVE);
        branchRepository.save(branch);
    }

    private ClinicBranchesDTO toDto(ClinicBranches b) {
        ClinicBranchesDTO dto = new ClinicBranchesDTO();
        dto.setBranchId(b.getBranchId());
        dto.setBranchName(b.getBranchName());
        dto.setAddress(b.getAddress());
        dto.setPhone(b.getPhone());
        dto.setEmail(b.getEmail());
        dto.setStatus(b.getStatus());
        return dto;
    }
}

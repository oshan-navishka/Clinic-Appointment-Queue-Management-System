package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.ClinicBranchesDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.service.ClinicBranchesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinicBranches")
@RequiredArgsConstructor
@Slf4j
public class ClinicBranchesController {

    private final ClinicBranchesService branchService;

    @PostMapping(value = "/addBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse addBranch(@RequestBody ClinicBranchesDTO dto) {
        log.info("Adding clinic branch: {}", dto.getBranchName());
        branchService.saveClinicBranches(dto);
        return new CommonResponse(0, "Branch added successfully");
    }

    @GetMapping(value = "/getAllBranches", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllBranches() {
        return new CommonResponse(0, branchService.getAllBranches(), "Branches loaded");
    }

    @PutMapping(value = "/updateBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateBranch(@RequestBody ClinicBranchesDTO dto) {
        log.info("Updating branch: {}", dto.getBranchId());
        branchService.updateBranch(dto);
        return new CommonResponse(0, "Branch updated successfully");
    }

    @DeleteMapping(value = "/deleteBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteBranch(@PathVariable String branchId) {
        log.info("Deleting branch: {}", branchId);
        branchService.deleteBranch(branchId);
        return new CommonResponse(0, "Branch deleted (set inactive)");
    }
}

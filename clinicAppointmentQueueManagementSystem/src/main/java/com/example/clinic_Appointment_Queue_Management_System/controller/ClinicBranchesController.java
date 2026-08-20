package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.ClinicBranchesDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.service.ClinicBranchesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/clinicBranches")
@RequiredArgsConstructor
public class ClinicBranchesController {
    private final ClinicBranchesService clinicBranchesService;

    @PostMapping(value = "/addBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse addClinicBranch(@RequestBody ClinicBranchesDTO clinicBranchesDTO) {
        log.info("Adding clinic branch");
        clinicBranchesService.saveClinicBranches(clinicBranchesDTO);
        return new CommonResponse(0, "Clinic branch added successfully");
    }
}

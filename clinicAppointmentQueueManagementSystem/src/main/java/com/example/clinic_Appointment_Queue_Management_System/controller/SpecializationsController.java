package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.SpecializationsDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.SpecializationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
@Slf4j
public class SpecializationsController {
    private final SpecializationsService specializationsService;

    public CommonResponse saveSpecialization(SpecializationsDTO specializationsDTO) {
        log.info("Saving specialization: {}", specializationsDTO.toString());
        specializationsService.saveSpecialization(specializationsDTO);
        return new CommonResponse(0, "Specialization saved successfully");
    }
}

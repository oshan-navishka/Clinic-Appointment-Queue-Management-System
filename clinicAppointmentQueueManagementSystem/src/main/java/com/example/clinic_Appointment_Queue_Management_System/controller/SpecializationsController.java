package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.SpecializationsDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.SpecializationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@Slf4j
@RequiredArgsConstructor
public class SpecializationsController {
    private final SpecializationsService specializationsService;

    @PostMapping(value = "/saveSpecialization", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveSpecialization(@RequestBody SpecializationsDTO specializationsDTO) {
        log.info("Save Specialization");
        specializationsService.saveSpecialization(specializationsDTO);
        return new CommonResponse(0, "Specialization saved successfully");
    }

    @PutMapping(value = "/updateSpecialization", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateSpecialization(@RequestBody SpecializationsDTO specializationsDTO) {
        log.info("Update Specialization");
        specializationsService.updateSpecialization(specializationsDTO);
        return new CommonResponse(0, "Specialization updated successfully");
    }

    @GetMapping(value = "/{specializationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getSpecializationById(@PathVariable String specializationId) {
        log.info("Get Specialization");
        List<SpecializationsDTO> specializationsDTOList = specializationsService.getAllSpecializations();
        return new CommonResponse(0, specializationsDTOList, "Specialization fetched successfully");
    }
}

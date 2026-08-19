package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.PatientDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {
    private final PatientService patientService;

    @PostMapping(value = "/savePatient", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse savePatient(@RequestBody PatientDTO patientDTO) {
        patientService.savePatient(patientDTO);
        return new CommonResponse(0, "Patient saved successfully");
    }
}

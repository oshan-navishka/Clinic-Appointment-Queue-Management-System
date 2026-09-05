package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.PatientDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {
    private final PatientService patientService;

    @PostMapping(value = "/savePatient", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse savePatient(@RequestBody PatientDTO patientDTO) {
        log.info("Saving Patient {}", patientDTO);
        patientService.savePatient(patientDTO);
        return new CommonResponse(0, "Patient saved successfully");
    }

    @GetMapping(value = "/allPatients", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllPatients() {
        log.info("Getting allPatients");
        List<PatientDTO> patientDTOS = patientService.getAllPatients();
        return new CommonResponse(0, patientDTOS, "Patients fetched successfully");
    }

    @PutMapping(value = "/updatePatient", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updatePatient(@RequestBody PatientDTO patientDTO) {
        log.info("Updating Patient {}", patientDTO);
        patientService.updatePatient(patientDTO);
        return new CommonResponse(0, "Patient updated successfully");
    }
}

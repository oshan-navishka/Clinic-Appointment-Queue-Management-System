package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping(value = "/addDct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse  addDoctor(@RequestBody DoctorDTO doctorDTO){
        log.info("Adding doctor: {}", doctorDTO);
        doctorService.addDoctor(doctorDTO);
        return new CommonResponse(0, "Doctor added successfully");
    }

    @GetMapping(value = "/loadAllDct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse loadAllDoctors() {
        log.info("Loading all doctors");
        List<DoctorDTO> doctorDTOList = doctorService.getAllDoctors();
        return new CommonResponse(0, doctorDTOList, "All doctors loaded successfully");
    }
}

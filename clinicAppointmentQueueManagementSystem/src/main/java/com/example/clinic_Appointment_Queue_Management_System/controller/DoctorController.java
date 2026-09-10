package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.AppointmentService;
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
    private final AppointmentService appointmentService;

    @PostMapping(value = "/addDct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse addDoctor(@RequestBody DoctorDTO doctorDTO) {
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

    @PutMapping(value = "/updateDoctor", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateDoctor(@RequestBody DoctorDTO doctorDTO) {
        log.info("Updating doctor: {}", doctorDTO);
        doctorService.updateDoctor(doctorDTO);
        return new CommonResponse(0, "Doctor updated successfully");
    }

    @GetMapping(value = "/byUser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByUser(@PathVariable String userId) {
        return new CommonResponse(0, doctorService.getByUserId(userId), "Doctor profile loaded");
    }

    @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDoctorDashboard(@RequestParam String userId) {
        log.info("Loading doctor dashboard for userId: {}", userId);
        return new CommonResponse(0, appointmentService.getDoctorDashboard(userId), "Doctor dashboard loaded");
    }

    @GetMapping(value = "/cards", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDoctorCards() {
        log.info("Loading doctor cards for patient booking");
        return new CommonResponse(0, doctorService.getDoctorsWithAvailabilityAndFee(), "Doctor cards loaded");
    }
}

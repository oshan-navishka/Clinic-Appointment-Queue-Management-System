package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.MedicalRecordDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicalRecords")
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveRecord(@RequestBody MedicalRecordDTO dto) {
        log.info("Saving medical record for appointment {}", dto.getAppointmentId());
        recordService.saveRecord(dto);
        return new CommonResponse(0, "Medical record saved successfully");
    }

    @GetMapping(value = "/byPatient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByPatient(@PathVariable String patientId) {
        return new CommonResponse(0, recordService.getByPatientId(patientId), "Records loaded");
    }

    @GetMapping(value = "/myRecords", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMyRecords(@RequestParam String userId) {
        return new CommonResponse(0, recordService.getByUserId(userId), "Your medical records loaded");
    }

    @GetMapping(value = "/byAppointment/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByAppointment(@PathVariable String appointmentId) {
        Object rec = recordService.getByAppointmentId(appointmentId);
        return new CommonResponse(0, rec, rec != null ? "Record loaded" : "No record for this appointment");
    }
}

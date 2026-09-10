package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.PatientMedicationDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientMedicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Slf4j
public class PatientMedicationController {

    private final PatientMedicationService medicationService;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveMedication(@RequestBody PatientMedicationDTO dto) {
        log.info("Saving medication {} for patient {}", dto.getMedicineName(), dto.getPatientId());
        medicationService.saveMedication(dto);
        return new CommonResponse(0, "Medication saved successfully");
    }

    @GetMapping(value = "/byPatient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByPatient(@PathVariable String patientId) {
        return new CommonResponse(0, medicationService.getByPatientId(patientId), "Medications loaded");
    }

    @GetMapping(value = "/myMedications", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMyMedications(@RequestParam String userId) {
        return new CommonResponse(0, medicationService.getApprovedByUserId(userId),
                "Your medications loaded");
    }

    @GetMapping(value = "/pendingApproval", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPendingApproval() {
        return new CommonResponse(0, medicationService.getPendingApproval(),
                "Pending approval medications loaded");
    }

    @PutMapping(value = "/approve/{medicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse approveMedication(@PathVariable String medicationId) {
        log.info("Admin approving medication {}", medicationId);
        medicationService.approveMedication(medicationId);
        return new CommonResponse(0, "Medication approved — patient can now view it");
    }

    @PutMapping(value = "/updateStatus/{medicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateStatus(@PathVariable String medicationId,
                                       @RequestParam String status) {
        log.info("Updating medication {} status to {}", medicationId, status);
        medicationService.updateStatus(medicationId, status);
        return new CommonResponse(0, "Status updated to " + status);
    }

    @DeleteMapping(value = "/{medicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteMedication(@PathVariable String medicationId) {
        log.info("Deleting medication {}", medicationId);
        medicationService.deleteMedication(medicationId);
        return new CommonResponse(0, "Medication deleted");
    }
}

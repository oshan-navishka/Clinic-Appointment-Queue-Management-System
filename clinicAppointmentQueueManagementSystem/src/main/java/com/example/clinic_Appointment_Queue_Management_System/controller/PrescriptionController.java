package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.PrescriptionDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.*;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.AppointmentState;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientMedicationRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Slf4j
public class PrescriptionController {

    private final PrescriptionRepository      prescriptionRepository;
    private final AppointmentRepository       appointmentRepository;
    private final DoctorRepository            doctorRepository;
    private final PatientMedicationRepository medicationRepository;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CommonResponse savePrescription(@RequestBody PrescriptionDTO dto) {
        log.info("Saving prescription for appointment {}", dto.getAppointmentId());

        Appointments appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + dto.getAppointmentId()));

        if (appointment.getAppointmentState() != AppointmentState.CHECKED) {
            throw new RuntimeException("Prescription can only be added after patient is marked as checked");
        }

        Doctor  doctor  = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        long pCount = prescriptionRepository.count();
        String pId  = String.format("PRE%03d", pCount + 1);

        StringBuilder details = new StringBuilder();
        if (dto.getMedicines() != null) {
            dto.getMedicines().forEach(m ->
                details.append(m.getMedicineName())
                       .append(" | ").append(m.getDosage())
                       .append(" | ").append(m.getFrequency()).append("\n")
            );
        }

        Prescription prescription = new Prescription();
        prescription.setPrescriptionId(pId);
        prescription.setAppointment(appointment);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setIssuedDate(dto.getIssuedDate() != null ? dto.getIssuedDate() : LocalDate.now());
        prescription.setValidUntil(dto.getValidUntil());
        prescription.setInstructions(dto.getInstructions());
        prescription.setMedicineDetails(details.toString());
        prescriptionRepository.save(prescription);

        if (dto.getMedicines() != null && !dto.getMedicines().isEmpty()) {
            long mBase = medicationRepository.count();
            int  idx   = 0;
            for (PrescriptionDTO.MedicineItemDTO med : dto.getMedicines()) {
                if (med.getMedicineName() == null || med.getMedicineName().isBlank()) continue;
                String mId = String.format("MED%03d", mBase + idx + 1);
                PatientMedication pm = new PatientMedication();
                pm.setMedicationId(mId);
                pm.setPatient(patient);
                pm.setPrescription(prescription);
                pm.setMedicineName(med.getMedicineName());
                pm.setDosage(med.getDosage());
                pm.setFrequency(med.getFrequency());
                pm.setStartDate(med.getStartDate() != null ? med.getStartDate() : LocalDate.now());
                pm.setEndDate(med.getEndDate());
                pm.setStatus("PENDING_APPROVAL");
                medicationRepository.save(pm);
                idx++;
            }
            log.info("Created {} medication records for patient {}", idx, patient.getPatientId());
        }

        return new CommonResponse(0,
            "Prescription saved and " + (dto.getMedicines() != null ? dto.getMedicines().size() : 0)
            + " medication(s) added to patient profile");
    }

    @GetMapping(value = "/byPatient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByPatient(@PathVariable String patientId) {
        List<PrescriptionDTO> list = prescriptionRepository.findByPatient_PatientId(patientId)
                .stream().map(this::toDto).collect(Collectors.toList());
        return new CommonResponse(0, list, "Prescriptions loaded");
    }

    @GetMapping(value = "/byAppointment/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByAppointment(@PathVariable String appointmentId) {
        List<PrescriptionDTO> list = prescriptionRepository.findByAppointment_AppointmentId(appointmentId)
                .stream().map(this::toDto).collect(Collectors.toList());
        return new CommonResponse(0, list, "Prescriptions loaded");
    }

    private PrescriptionDTO toDto(Prescription p) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPrescriptionId(p.getPrescriptionId());
        dto.setAppointmentId(p.getAppointment().getAppointmentId());
        dto.setPatientId(p.getPatient().getPatientId());
        dto.setPatientName(p.getPatient().getFirstName() + " " + p.getPatient().getLastName());
        dto.setDoctorId(p.getDoctor().getDoctorId());
        dto.setDoctorName(p.getDoctor().getFirstName() + " " + p.getDoctor().getLastName());
        dto.setIssuedDate(p.getIssuedDate());
        dto.setValidUntil(p.getValidUntil());
        dto.setInstructions(p.getInstructions());
        return dto;
    }
}

package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientMedicationDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.PatientMedication;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientMedicationRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PrescriptionRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.NotificationService;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientMedicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientMedicationServiceImpl implements PatientMedicationService {

    private final PatientMedicationRepository medicationRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void saveMedication(PatientMedicationDTO dto) {
        log.info("Saving medication {} for patient {}", dto.getMedicineName(), dto.getPatientId());
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));

        long count = medicationRepository.count();
        String id  = String.format("MED%03d", count + 1);

        PatientMedication med = new PatientMedication();
        med.setMedicationId(id);
        med.setPatient(patient);
        med.setMedicineName(dto.getMedicineName());
        med.setDosage(dto.getDosage());
        med.setFrequency(dto.getFrequency());
        med.setStartDate(dto.getStartDate());
        med.setEndDate(dto.getEndDate());
        med.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        if (dto.getPrescriptionId() != null && !dto.getPrescriptionId().isBlank()) {
            prescriptionRepository.findById(dto.getPrescriptionId()).ifPresent(med::setPrescription);
        }
        medicationRepository.save(med);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getByPatientId(String patientId) {
        return medicationRepository.findByPatientId(patientId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getByUserId(String userId) {
        return medicationRepository.findByUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getApprovedByUserId(String userId) {
        return medicationRepository.findApprovedByUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getPendingApproval() {
        return medicationRepository.findAllPendingApproval()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveMedication(String medicationId) {
        PatientMedication med = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("Medication not found: " + medicationId));
        if (!"PENDING_APPROVAL".equals(med.getStatus()))
            throw new RuntimeException("Medication is not pending approval");

        med.setStatus("ACTIVE");
        medicationRepository.save(med);
        log.info("Medication {} approved — status set to ACTIVE", medicationId);

        try {
            String patientUserId = med.getPatient().getUser().getUserId();
            notificationService.createNotification(
                    patientUserId,
                    "Medication Approved",
                    "Your prescription has been approved by the clinic. " +
                    med.getMedicineName() + " (" + med.getDosage() + " — " +
                    med.getFrequency() + ") is now visible in your medications.",
                    "MEDICATION_APPROVED",
                    medicationId
            );
        } catch (Exception e) {
            log.warn("Could not send medication approval notification: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateStatus(String medicationId, String status) {
        PatientMedication med = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("Medication not found: " + medicationId));
        med.setStatus(status);
        medicationRepository.save(med);
        log.info("Medication {} status updated to {}", medicationId, status);
    }

    @Override
    @Transactional
    public void deleteMedication(String medicationId) {
        if (!medicationRepository.existsById(medicationId))
            throw new RuntimeException("Medication not found: " + medicationId);
        medicationRepository.deleteById(medicationId);
    }

    private PatientMedicationDTO toDto(PatientMedication m) {
        PatientMedicationDTO dto = new PatientMedicationDTO();
        dto.setMedicationId(m.getMedicationId());
        dto.setPatientId(m.getPatient().getPatientId());
        dto.setPatientName(m.getPatient().getFirstName() + " " + m.getPatient().getLastName());
        dto.setMedicineName(m.getMedicineName());
        dto.setDosage(m.getDosage());
        dto.setFrequency(m.getFrequency());
        dto.setStartDate(m.getStartDate());
        dto.setEndDate(m.getEndDate());
        dto.setStatus(m.getStatus());
        if (m.getPrescription() != null) {
            dto.setPrescriptionId(m.getPrescription().getPrescriptionId());
            if (m.getPrescription().getAppointment() != null)
                dto.setAppointmentId(m.getPrescription().getAppointment().getAppointmentId());
        }
        return dto;
    }
}

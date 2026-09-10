package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientMedicationDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.PatientMedication;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
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
    private final PatientRepository           patientRepository;
    private final PrescriptionRepository      prescriptionRepository;
    private final NotificationService         notificationService;

    @Override
    @Transactional
    public void saveMedication(PatientMedicationDTO dto) {
        log.info("Saving medication {} for patient {}", dto.getMedicineName(), dto.getPatientId());
        try {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new CustomException(404, "Patient not found"));

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
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving medication", e);
            throw new CustomException(500, "Error occurred while saving medication");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getByPatientId(String patientId) {
        try {
            return medicationRepository.findByPatientId(patientId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching medications for patient {}", patientId, e);
            throw new CustomException(500, "Error occurred while fetching medications");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getByUserId(String userId) {
        try {
            return medicationRepository.findByUserId(userId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching medications for user {}", userId, e);
            throw new CustomException(500, "Error occurred while fetching medications");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getApprovedByUserId(String userId) {
        try {
            return medicationRepository.findApprovedByUserId(userId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching approved medications for user {}", userId, e);
            throw new CustomException(500, "Error occurred while fetching approved medications");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientMedicationDTO> getPendingApproval() {
        try {
            return medicationRepository.findAllPendingApproval()
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching pending medications", e);
            throw new CustomException(500, "Error occurred while fetching pending medications");
        }
    }

    @Override
    @Transactional
    public void approveMedication(String medicationId) {
        log.info("Approving medication {}", medicationId);
        try {
            PatientMedication med = medicationRepository.findById(medicationId)
                    .orElseThrow(() -> new CustomException(404, "Medication not found"));
            if (!"PENDING_APPROVAL".equals(med.getStatus())) {
                throw new CustomException(400, "Medication is not pending approval");
            }
            med.setStatus("ACTIVE");
            medicationRepository.save(med);
            log.info("Medication {} approved", medicationId);

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
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error approving medication {}", medicationId, e);
            throw new CustomException(500, "Error occurred while approving medication");
        }
    }

    @Override
    @Transactional
    public void updateStatus(String medicationId, String status) {
        log.info("Updating medication {} status to {}", medicationId, status);
        try {
            PatientMedication med = medicationRepository.findById(medicationId)
                    .orElseThrow(() -> new CustomException(404, "Medication not found"));
            med.setStatus(status);
            medicationRepository.save(med);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating medication status", e);
            throw new CustomException(500, "Error occurred while updating medication status");
        }
    }

    @Override
    @Transactional
    public void deleteMedication(String medicationId) {
        log.info("Deleting medication {}", medicationId);
        try {
            if (!medicationRepository.existsById(medicationId)) {
                throw new CustomException(404, "Medication not found");
            }
            medicationRepository.deleteById(medicationId);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting medication {}", medicationId, e);
            throw new CustomException(500, "Error occurred while deleting medication");
        }
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

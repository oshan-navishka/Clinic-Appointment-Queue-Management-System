package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.MedicalRecordDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Appointments;
import com.example.clinic_Appointment_Queue_Management_System.entity.MedicalRecord;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.AppointmentState;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.MedicalRecordRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final AppointmentRepository   appointmentRepository;
    private final PatientRepository       patientRepository;
    private final DoctorRepository        doctorRepository;

    @Override
    @Transactional
    public void saveRecord(MedicalRecordDTO dto) {
        log.info("Saving medical record for appointment {}", dto.getAppointmentId());
        try {
            Appointments appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new CustomException(404, "Appointment not found"));

            if (appointment.getAppointmentState() != AppointmentState.CHECKED) {
                throw new CustomException(400, "Medical record can only be added for CHECKED appointments");
            }

            MedicalRecord record = recordRepository
                    .findByAppointment_AppointmentId(dto.getAppointmentId())
                    .orElseGet(() -> {
                        MedicalRecord r = new MedicalRecord();
                        long count = recordRepository.count();
                        r.setRecordId(String.format("REC%03d", count + 1));
                        r.setAppointment(appointment);
                        r.setPatient(appointment.getPatient());
                        r.setDoctor(appointment.getDoctor());
                        return r;
                    });

            record.setVisitDate(dto.getVisitDate() != null ? dto.getVisitDate() : appointment.getAppointmentDate());
            record.setDiagnosis(dto.getDiagnosis());
            record.setTreatment(dto.getTreatment());
            record.setNotes(dto.getNotes());
            recordRepository.save(record);
            log.info("Medical record {} saved", record.getRecordId());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving medical record", e);
            throw new CustomException(500, "Error occurred while saving medical record");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getByPatientId(String patientId) {
        try {
            return recordRepository.findByPatientId(patientId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching records for patient {}", patientId, e);
            throw new CustomException(500, "Error occurred while fetching medical records");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getByUserId(String userId) {
        try {
            return recordRepository.findByUserId(userId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching records for user {}", userId, e);
            throw new CustomException(500, "Error occurred while fetching medical records");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordDTO getByAppointmentId(String appointmentId) {
        try {
            return recordRepository.findByAppointment_AppointmentId(appointmentId)
                    .map(this::toDto)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error fetching record for appointment {}", appointmentId, e);
            throw new CustomException(500, "Error occurred while fetching medical record");
        }
    }

    private MedicalRecordDTO toDto(MedicalRecord r) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setRecordId(r.getRecordId());
        dto.setPatientId(r.getPatient().getPatientId());
        dto.setPatientName(r.getPatient().getFirstName() + " " + r.getPatient().getLastName());
        dto.setPatientContact(r.getPatient().getContact());
        dto.setPatientAge(r.getPatient().getAge());
        dto.setPatientGender(r.getPatient().getGender() != null ? r.getPatient().getGender().name() : null);
        dto.setDoctorId(r.getDoctor().getDoctorId());
        dto.setDoctorName(r.getDoctor().getFirstName() + " " + r.getDoctor().getLastName());
        if (r.getDoctor().getSpecializations() != null)
            dto.setSpecializationName(r.getDoctor().getSpecializations().getName());
        if (r.getAppointment() != null)
            dto.setAppointmentId(r.getAppointment().getAppointmentId());
        dto.setVisitDate(r.getVisitDate());
        dto.setDiagnosis(r.getDiagnosis());
        dto.setTreatment(r.getTreatment());
        dto.setNotes(r.getNotes());
        return dto;
    }
}

package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Gender;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Override
    public void savePatient(PatientDTO patientDTO) {
        log.info("Saving Patient {}", patientDTO);
        try{
            User user = userRepository.findById(patientDTO.getUserId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found with ID: " + patientDTO.getUserId()));

            long count = patientRepository.count();
            String generatedId = String.format("P%03d", count + 1);

            Patient patient = new Patient();
            patient.setPatientId(generatedId);
            patient.setUser(user);

            patient.setFirstName(patientDTO.getFirstName());
            patient.setLastName(patientDTO.getLastName());
            patient.setAge(patientDTO.getAge());
            patient.setGender(Gender.valueOf(patientDTO.getGender().toUpperCase()));
            patient.setContact(patientDTO.getContact());
            patient.setAddress(patientDTO.getAddress());
            patient.setEmergencyContact(patientDTO.getEmergencyContact());
            patient.setStatus(Status.ACTIVE);

            patientRepository.save(patient);

        } catch (Exception e) {
            log.error("Error occurred while saving patient {}", patientDTO, e);
            throw new RuntimeException("Error occurred while saving patient", e);
        }
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        log.info("loading all patients");
        try{
            List<PatientDTO> patientDTOS = new ArrayList<>();
            List<Patient> patients = patientRepository.findAll();

            for (Patient patient : patients) {
                PatientDTO patientDTO = new PatientDTO();
                patientDTO.setPatientId(patient.getPatientId());
                patientDTO.setUserId(patient.getUser().getUserId());
                patientDTO.setFirstName(patient.getFirstName());
                patientDTO.setLastName(patient.getLastName());
                patientDTO.setAge(patient.getAge());
                patientDTO.setGender(patient.getGender().name());
                patientDTO.setContact(patient.getContact());
                patientDTO.setAddress(patient.getAddress());
                patientDTO.setEmergencyContact(patient.getEmergencyContact());
                patientDTO.setStatus(patient.getStatus());
                patientDTOS.add(patientDTO);
            }
            return patientDTOS;

        } catch (Exception e) {
            log.error("Error occurred while fetching all patients", e);
            throw new RuntimeException("Error occurred while fetching all patients", e);
        }
    }

    @Override
    public void updatePatient(PatientDTO patientDTO) {
        log.info("Updating Patient {}", patientDTO);
        try{
            Optional<Patient> optionalPatient = patientRepository.findById(patientDTO.getPatientId());

            if (optionalPatient.isEmpty())
                throw new RuntimeException("Patient not found with ID: " + patientDTO.getPatientId());

            Patient patient = optionalPatient.get();
            patient.setFirstName(patientDTO.getFirstName());
            patient.setLastName(patientDTO.getLastName());
            patient.setAge(patientDTO.getAge());
            patient.setGender(Gender.valueOf(patientDTO.getGender().toUpperCase()));
            patient.setContact(patientDTO.getContact());
            patient.setAddress(patientDTO.getAddress());
            patient.setEmergencyContact(patientDTO.getEmergencyContact());
            patient.setStatus(Status.ACTIVE);
            patientRepository.save(patient);
            log.info("Patient updated successfully: {}", patient.getPatientId());
        } catch (Exception e) {
            log.error("Error occurred while updating patient {}", patientDTO, e);
            throw new RuntimeException("Error occurred while updating patient", e);
        }
    }
}

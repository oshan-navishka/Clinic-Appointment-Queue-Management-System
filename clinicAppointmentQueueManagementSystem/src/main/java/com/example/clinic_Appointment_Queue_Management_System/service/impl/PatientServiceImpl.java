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
            patientRepository.save(patient);

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
}

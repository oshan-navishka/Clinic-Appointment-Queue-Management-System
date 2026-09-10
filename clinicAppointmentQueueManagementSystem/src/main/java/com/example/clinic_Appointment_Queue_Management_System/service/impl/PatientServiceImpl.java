package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Gender;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.EmailService;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public void savePatient(PatientDTO patientDTO) {
        log.info("Saving Patient {}", patientDTO);
        try{
            User user = resolveOrCreatePatientUser(patientDTO, false);

            long count = patientRepository.count();
            String generatedId = String.format("P%03d", count + 1);

            Patient patient = new Patient();
            patient.setPatientId(generatedId);
            patient.setUser(user);

            patient.setFirstName(patientDTO.getFirstName());
            patient.setLastName(patientDTO.getLastName());
            patient.setAge(patientDTO.getAge() != null ? patientDTO.getAge() : 0);
            patient.setGender(parseGender(patientDTO.getGender()));
            patient.setContact(patientDTO.getContact());
            patient.setAddress(patientDTO.getAddress());
            patient.setEmergencyContact(patientDTO.getEmergencyContact());
            patient.setStatus(Status.ACTIVE);

            patientRepository.saveAndFlush(patient);

            try {
                String email = user.getUserEmail();
                if (email != null && !email.isBlank()
                        && patientDTO.getPassword() != null && !patientDTO.getPassword().isBlank()) {
                    String fullName = patientDTO.getFirstName() + " " + patientDTO.getLastName();
                    emailService.sendWelcomeEmail(email, fullName,
                            user.getUsername(), patientDTO.getPassword(), "PATIENT");
                }
            } catch (Exception ex) {
                log.warn("Could not send welcome email for patient: {}", ex.getMessage());
            }

        } catch (Exception e) {
            log.error("Error occurred while saving patient {}", patientDTO, e);
            throw new RuntimeException("Error occurred while saving patient", e);
        }
    }

    @Override
    public void selfRegister(PatientDTO patientDTO) {
        log.info("Patient self register {}", patientDTO.getUserEmail());
        patientDTO.setUserId(null);
        if (patientDTO.getUserEmail() == null || patientDTO.getUserEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (patientDTO.getPassword() == null || patientDTO.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }
        if (patientDTO.getUsername() == null || patientDTO.getUsername().isBlank()) {
            patientDTO.setUsername(patientDTO.getUserEmail());
        }
        savePatient(patientDTO);
    }

    @Override
    public PatientDTO getByUserId(String userId) {
        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user: " + userId));
        return toDto(patient);
    }

    private User resolveOrCreatePatientUser(PatientDTO patientDTO, boolean forceNew) {
        if (!forceNew && patientDTO.getUserId() != null && !patientDTO.getUserId().isBlank()) {
            return userRepository.findById(patientDTO.getUserId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found with ID: " + patientDTO.getUserId()));
        }

        String username = patientDTO.getUsername();
        if (username == null || username.isBlank()) {
            username = patientDTO.getUserEmail();
        }
        if (username == null || username.isBlank() || patientDTO.getPassword() == null || patientDTO.getPassword().isBlank()) {
            throw new RuntimeException("Username/email and password are required to create a patient login");
        }
        if (userRepository.existsByUsername(username) ||
                (patientDTO.getUserEmail() != null && userRepository.existsByUserEmail(patientDTO.getUserEmail()))) {
            throw new RuntimeException("An account already exists with this email or username");
        }

        long count = userRepository.count();
        String generatedId = String.format("U%03d", count + 1);
        User user = new User();
        user.setUserId(generatedId);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(patientDTO.getPassword()));
        user.setUserEmail(patientDTO.getUserEmail() != null ? patientDTO.getUserEmail() : username);
        user.setUserRole(UserRole.PATIENT);
        user.setStatus(Status.ACTIVE);
        return userRepository.save(user);
    }

    private Gender parseGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return Gender.OTHER;
        }
        return Gender.valueOf(gender.toUpperCase());
    }

    private PatientDTO toDto(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setPatientId(patient.getPatientId());
        patientDTO.setUserId(patient.getUser().getUserId());
        patientDTO.setFirstName(patient.getFirstName());
        patientDTO.setLastName(patient.getLastName());
        patientDTO.setAge(patient.getAge());
        patientDTO.setGender(patient.getGender() != null ? patient.getGender().name() : null);
        patientDTO.setContact(patient.getContact());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setEmergencyContact(patient.getEmergencyContact());
        patientDTO.setStatus(patient.getStatus());
        patientDTO.setUserEmail(patient.getUser().getUserEmail());
        return patientDTO;
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        log.info("loading all patients");
        try{
            List<PatientDTO> patientDTOS = new ArrayList<>();
            List<Patient> patients = patientRepository.findAll();

            for (Patient patient : patients) {
                PatientDTO patientDTO = toDto(patient);
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

    @Override
    public void deletePatient(String patientId) {
        log.info("Deleting Patient {}", patientId);
        try{
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);

            if (optionalPatient.isEmpty())
                throw new RuntimeException("Patient not found with ID: " + patientId);
            Patient patient = optionalPatient.get();
            patient.setStatus(Status.INACTIVE);
            patientRepository.save(patient);
            log.info("Patient deleted successfully: {}", patientId);
        } catch (Exception e) {
            log.error("Error occurred while deleting patient {}", patientId, e);
            throw new RuntimeException("Error occurred while deleting patient", e);
        }
    }

    @Override
    public List<PatientDTO> searchPatients(String keyword) {
        List<Patient> patients = patientRepository.searchPatients(keyword);
        List<PatientDTO> patientDTOS = new ArrayList<>();
        for (Patient patient : patients) {
            patientDTOS.add(toDto(patient));
        }
        return patientDTOS;
    }
}

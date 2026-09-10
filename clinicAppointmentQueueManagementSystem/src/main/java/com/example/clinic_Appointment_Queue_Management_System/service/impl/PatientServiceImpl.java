package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.PatientDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Gender;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
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
    private final UserRepository    userRepository;
    private final PasswordEncoder   passwordEncoder;
    private final EmailService      emailService;

    @Override
    public void savePatient(PatientDTO patientDTO) {
        log.info("Saving patient {}", patientDTO);
        try {
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
                    emailService.sendWelcomeEmail(email,
                            patientDTO.getFirstName() + " " + patientDTO.getLastName(),
                            user.getUsername(), patientDTO.getPassword(), "PATIENT");
                }
            } catch (Exception ex) {
                log.warn("Could not send welcome email for patient: {}", ex.getMessage());
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving patient", e);
            throw new CustomException(500, "Error occurred while saving patient");
        }
    }

    @Override
    public void selfRegister(PatientDTO patientDTO) {
        log.info("Patient self register {}", patientDTO.getUserEmail());
        patientDTO.setUserId(null);
        if (patientDTO.getUserEmail() == null || patientDTO.getUserEmail().isBlank()) {
            throw new CustomException(400, "Email is required");
        }
        if (patientDTO.getPassword() == null || patientDTO.getPassword().isBlank()) {
            throw new CustomException(400, "Password is required");
        }
        if (patientDTO.getUsername() == null || patientDTO.getUsername().isBlank()) {
            patientDTO.setUsername(patientDTO.getUserEmail());
        }
        savePatient(patientDTO);
    }

    @Override
    public PatientDTO getByUserId(String userId) {
        try {
            Patient patient = patientRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new CustomException(404, "Patient profile not found"));
            return toDto(patient);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching patient for user {}", userId, e);
            throw new CustomException(500, "Error occurred while fetching patient profile");
        }
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        log.info("Fetching all patients");
        try {
            List<PatientDTO> list = new ArrayList<>();
            for (Patient patient : patientRepository.findAll()) {
                list.add(toDto(patient));
            }
            return list;
        } catch (Exception e) {
            log.error("Error fetching all patients", e);
            throw new CustomException(500, "Error occurred while fetching patients");
        }
    }

    @Override
    public void updatePatient(PatientDTO patientDTO) {
        log.info("Updating patient {}", patientDTO.getPatientId());
        try {
            Optional<Patient> optionalPatient = patientRepository.findById(patientDTO.getPatientId());
            if (optionalPatient.isEmpty()) {
                throw new CustomException(404, "Patient not found");
            }
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
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating patient {}", patientDTO.getPatientId(), e);
            throw new CustomException(500, "Error occurred while updating patient");
        }
    }

    @Override
    public void deletePatient(String patientId) {
        log.info("Deleting patient {}", patientId);
        try {
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);
            if (optionalPatient.isEmpty()) {
                throw new CustomException(404, "Patient not found");
            }
            Patient patient = optionalPatient.get();
            patient.setStatus(Status.INACTIVE);
            patientRepository.save(patient);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting patient {}", patientId, e);
            throw new CustomException(500, "Error occurred while deleting patient");
        }
    }

    @Override
    public List<PatientDTO> searchPatients(String keyword) {
        try {
            List<PatientDTO> list = new ArrayList<>();
            for (Patient patient : patientRepository.searchPatients(keyword)) {
                list.add(toDto(patient));
            }
            return list;
        } catch (Exception e) {
            log.error("Error searching patients with keyword {}", keyword, e);
            throw new CustomException(500, "Error occurred while searching patients");
        }
    }

    private User resolveOrCreatePatientUser(PatientDTO patientDTO, boolean forceNew) {
        if (!forceNew && patientDTO.getUserId() != null && !patientDTO.getUserId().isBlank()) {
            return userRepository.findById(patientDTO.getUserId())
                    .orElseThrow(() -> new CustomException(404, "User not found"));
        }
        String username = patientDTO.getUsername();
        if (username == null || username.isBlank()) {
            username = patientDTO.getUserEmail();
        }
        if (username == null || username.isBlank()
                || patientDTO.getPassword() == null || patientDTO.getPassword().isBlank()) {
            throw new CustomException(400, "Username/email and password are required to create a patient login");
        }
        if (userRepository.existsByUsername(username) ||
                (patientDTO.getUserEmail() != null && userRepository.existsByUserEmail(patientDTO.getUserEmail()))) {
            throw new CustomException(409, "An account already exists with this email or username");
        }

        long count = userRepository.count();
        User user = new User();
        user.setUserId(String.format("U%03d", count + 1));
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(patientDTO.getPassword()));
        user.setUserEmail(patientDTO.getUserEmail() != null ? patientDTO.getUserEmail() : username);
        user.setUserRole(UserRole.PATIENT);
        user.setStatus(Status.ACTIVE);
        return userRepository.save(user);
    }

    private Gender parseGender(String gender) {
        if (gender == null || gender.isBlank()) return Gender.OTHER;
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Gender.OTHER;
        }
    }

    private PatientDTO toDto(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setPatientId(patient.getPatientId());
        dto.setUserId(patient.getUser().getUserId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender() != null ? patient.getGender().name() : null);
        dto.setContact(patient.getContact());
        dto.setAddress(patient.getAddress());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setStatus(patient.getStatus());
        dto.setUserEmail(patient.getUser().getUserEmail());
        return dto;
    }
}

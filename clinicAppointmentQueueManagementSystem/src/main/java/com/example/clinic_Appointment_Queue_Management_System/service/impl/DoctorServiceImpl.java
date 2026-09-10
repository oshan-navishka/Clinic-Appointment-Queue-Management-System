package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorAvailabilityDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorCardDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.Specializations;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.repository.*;
import com.example.clinic_Appointment_Queue_Management_System.service.DoctorService;
import com.example.clinic_Appointment_Queue_Management_System.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private static final double DEFAULT_FEE = 2500.00;

    private final DoctorRepository             doctorRepository;
    private final UserRepository               userRepository;
    private final SpecializationsRepository    specializationsRepository;
    private final PasswordEncoder              passwordEncoder;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentFeeRepository     feeRepository;
    private final RatingRepository             ratingRepository;
    private final EmailService                 emailService;


    @Override
    public void addDoctor(DoctorDTO doctorDTO) {
        log.info("Adding doctor");
        try {
            User user = resolveOrCreateDoctorUser(doctorDTO);

            Specializations specialization = specializationsRepository
                    .findById(doctorDTO.getSpecializationId())
                    .orElseThrow(() -> new RuntimeException(
                            "Specialization not found: " + doctorDTO.getSpecializationId()));

            long count = doctorRepository.count();
            String generatedId = String.format("D%03d", count + 1);

            Doctor doctor = new Doctor();
            doctor.setDoctorId(generatedId);
            doctor.setUser(user);
            doctor.setFirstName(doctorDTO.getFirstName());
            doctor.setLastName(doctorDTO.getLastName());
            doctor.setSpecializations(specialization);
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
            doctor.setPhoneNumber(doctorDTO.getPhoneNumber());
            doctor.setEmail(doctorDTO.getEmail());
            doctor.setStatus(Status.ACTIVE);
            doctorRepository.save(doctor);

            try {
                String email = user.getUserEmail();
                if (email != null && !email.isBlank()
                        && doctorDTO.getPassword() != null && !doctorDTO.getPassword().isBlank()) {
                    String fullName = "Dr. " + doctorDTO.getFirstName() + " " + doctorDTO.getLastName();
                    emailService.sendWelcomeEmail(email, fullName,
                            user.getUsername(), doctorDTO.getPassword(), "DOCTOR");
                }
            } catch (Exception ex) {
                log.warn("Could not send welcome email for doctor: {}", ex.getMessage());
            }
        } catch (Exception e) {
            log.error("Doctor could not be added", e);
            throw e;
        }
    }


    @Override
    public List<DoctorDTO> getAllDoctors() {
        log.info("Fetching all doctors");
        try {
            List<DoctorDTO> list = new ArrayList<>();
            for (Doctor d : doctorRepository.findAll()) {
                DoctorDTO dto = new DoctorDTO();
                dto.setDoctorId(d.getDoctorId());
                dto.setUserId(d.getUser().getUserId());
                dto.setFirstName(d.getFirstName());
                dto.setLastName(d.getLastName());
                dto.setSpecializationName(d.getSpecializations().getName());
                dto.setSpecializationId(d.getSpecializations().getSpecializationId());
                dto.setLicenseNumber(d.getLicenseNumber());
                dto.setPhoneNumber(d.getPhoneNumber());
                dto.setEmail(d.getEmail());
                dto.setStatus(d.getStatus());
                list.add(dto);
            }
            return list;
        } catch (Exception e) {
            log.error("Error fetching all doctors", e);
            throw e;
        }
    }


    @Override
    public void updateDoctor(DoctorDTO doctorDTO) {
        log.info("Updating doctor {}", doctorDTO.getDoctorId());
        try {
            Doctor doctor = doctorRepository.findById(doctorDTO.getDoctorId())
                    .orElseThrow(() -> new RuntimeException(
                            "Doctor not found: " + doctorDTO.getDoctorId()));
            Specializations specialization = specializationsRepository
                    .findById(doctorDTO.getSpecializationId())
                    .orElseThrow(() -> new RuntimeException(
                            "Specialization not found: " + doctorDTO.getSpecializationId()));

            doctor.setFirstName(doctorDTO.getFirstName());
            doctor.setLastName(doctorDTO.getLastName());
            doctor.setSpecializations(specialization);
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
            doctor.setPhoneNumber(doctorDTO.getPhoneNumber());
            doctor.setEmail(doctorDTO.getEmail());
            doctor.setStatus(Status.ACTIVE);
            doctorRepository.save(doctor);
        } catch (Exception e) {
            log.error("Doctor could not be updated", e);
            throw e;
        }
    }


    @Override
    public DoctorDTO getByUserId(String userId) {
        Doctor d = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Doctor profile not found for user: " + userId));
        DoctorDTO dto = new DoctorDTO();
        dto.setDoctorId(d.getDoctorId());
        dto.setUserId(d.getUser().getUserId());
        dto.setFirstName(d.getFirstName());
        dto.setLastName(d.getLastName());
        dto.setSpecializationName(d.getSpecializations().getName());
        dto.setSpecializationId(d.getSpecializations().getSpecializationId());
        dto.setLicenseNumber(d.getLicenseNumber());
        dto.setPhoneNumber(d.getPhoneNumber());
        dto.setEmail(d.getEmail());
        dto.setStatus(d.getStatus());
        return dto;
    }


    @Override
    public List<DoctorCardDTO> getDoctorsWithAvailabilityAndFee() {
        return doctorRepository.findAll().stream()
                .filter(d -> d.getStatus() == Status.ACTIVE)
                .map(d -> {
                    try {
                        DoctorCardDTO card = new DoctorCardDTO();
                        card.setDoctorId(d.getDoctorId());
                        card.setFirstName(d.getFirstName());
                        card.setLastName(d.getLastName());
                        card.setFullName(("Dr. " + d.getFirstName() + " " + d.getLastName()).trim());

                        if (d.getSpecializations() != null) {
                            card.setSpecializationId(d.getSpecializations().getSpecializationId());
                            card.setSpecializationName(d.getSpecializations().getName());
                        } else {
                            card.setSpecializationId("");
                            card.setSpecializationName("General");
                        }

                        card.setLicenseNumber(d.getLicenseNumber());
                        card.setPhoneNumber(d.getPhoneNumber());
                        card.setEmail(d.getEmail());

                        try {
                            List<DoctorAvailabilityDTO> slots = availabilityRepository
                                    .findByDoctor_DoctorIdAndAvailableTrue(d.getDoctorId())
                                    .stream().map(av -> {
                                        DoctorAvailabilityDTO s = new DoctorAvailabilityDTO();
                                        s.setAvailabilityId(av.getAvailabilityId());
                                        s.setDoctorId(d.getDoctorId());
                                        s.setDayOfWeek(av.getDayOfWeek());
                                        s.setStartTime(av.getStartTime());
                                        s.setEndTime(av.getEndTime());
                                        s.setMaxSlots(av.getMaxSlots());
                                        s.setAvailable(av.isAvailable());
                                        if (av.getBranch() != null) {
                                            s.setBranchId(av.getBranch().getBranchId());
                                            s.setBranchName(av.getBranch().getBranchName());
                                        }
                                        return s;
                                    }).collect(Collectors.toList());
                            card.setAvailableSlots(slots);
                        } catch (Exception ex) {
                            log.warn("Could not load slots for doctor {}: {}", d.getDoctorId(), ex.getMessage());
                            card.setAvailableSlots(new ArrayList<>());
                        }

                        try {
                            feeRepository.findByDoctor_DoctorId(d.getDoctorId()).ifPresentOrElse(
                                    fee -> {
                                        card.setConsultationFee(fee.getConsultationFee());
                                        card.setCurrency(fee.getCurrency() != null ? fee.getCurrency() : "LKR");
                                    },
                                    () -> { card.setConsultationFee(DEFAULT_FEE); card.setCurrency("LKR"); }
                            );
                        } catch (Exception ex) {
                            log.warn("Could not load fee for doctor {}: {}", d.getDoctorId(), ex.getMessage());
                            card.setConsultationFee(DEFAULT_FEE);
                            card.setCurrency("LKR");
                        }

                        try {
                            Double avg   = ratingRepository.findAverageRatingByDoctorId(d.getDoctorId());
                            long   total = ratingRepository.countByDoctorId(d.getDoctorId());
                            card.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
                            card.setTotalRatings(total);
                        } catch (Exception ex) {
                            log.warn("Could not load ratings for doctor {}: {}", d.getDoctorId(), ex.getMessage());
                            card.setAverageRating(0.0);
                            card.setTotalRatings(0L);
                        }

                        return card;

                    } catch (Exception ex) {
                        log.error("Error building card for doctor {}: {}", d.getDoctorId(), ex.getMessage());
                        DoctorCardDTO fallback = new DoctorCardDTO();
                        fallback.setDoctorId(d.getDoctorId());
                        fallback.setFirstName(d.getFirstName());
                        fallback.setLastName(d.getLastName());
                        fallback.setFullName(("Dr. " + d.getFirstName() + " " + d.getLastName()).trim());
                        fallback.setSpecializationName("—");
                        fallback.setConsultationFee(DEFAULT_FEE);
                        fallback.setCurrency("LKR");
                        fallback.setAverageRating(0.0);
                        fallback.setTotalRatings(0L);
                        fallback.setAvailableSlots(new ArrayList<>());
                        return fallback;
                    }
                }).collect(Collectors.toList());
    }


    private User resolveOrCreateDoctorUser(DoctorDTO dto) {
        if (dto.getUserId() != null && !dto.getUserId().isBlank()) {
            return userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));
        }
        if (dto.getUsername() == null || dto.getUsername().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Doctor username and password are required");
        }
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("Username already exists");

        String email = dto.getUserEmail() != null && !dto.getUserEmail().isBlank()
                ? dto.getUserEmail() : dto.getEmail();
        if (email == null || email.isBlank())
            throw new RuntimeException("Doctor email is required");
        if (userRepository.existsByUserEmail(email))
            throw new RuntimeException("Email already exists");

        long count = userRepository.count();
        String uid = String.format("U%03d", count + 1);
        User user = new User();
        user.setUserId(uid);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserEmail(email);
        user.setUserRole(UserRole.DOCTOR);
        user.setStatus(Status.ACTIVE);
        return userRepository.save(user);
    }
}

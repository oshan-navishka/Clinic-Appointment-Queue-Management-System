package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.Specializations;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.SpecializationsRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationsRepository specializationsRepository;
    @Override
    public void addDoctor(DoctorDTO doctorDTO) {
        log.info("Doctor added to the queue");
        try {

            User user = userRepository.findById(doctorDTO.getUserId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found with ID: " + doctorDTO.getUserId()));

            Specializations specialization = specializationsRepository.findById(doctorDTO.getSpecializationId())
                    .orElseThrow(() ->
                            new RuntimeException("Specialization not found with ID: " + doctorDTO.getSpecializationId()));

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

            log.info("EMAIL FROM DTO = [{}]", doctorDTO.getEmail());

            doctor.setEmail(doctorDTO.getEmail());

            log.info("EMAIL IN ENTITY = [{}]", doctor.getEmail());

            doctorRepository.save(doctor);
        }catch (Exception e){
            log.error("Doctor could not be added", e);
            throw e;
        }
    }

    @Override
    public List<DoctorDTO> getAllDoctors() {
        log.info("Fetching all doctors");
        try{
            List<DoctorDTO> doctorDTOS = new ArrayList<>();
            List<Doctor> doctors = doctorRepository.findAll();
            for (Doctor doctor : doctors) {
                DoctorDTO doctorDTO = new DoctorDTO();
                doctorDTO.setDoctorId(doctor.getDoctorId());
                doctorDTO.setUserId(doctor.getUser().getUserId());
                doctorDTO.setFirstName(doctor.getFirstName());
                doctorDTO.setLastName(doctor.getLastName());
                doctorDTO.setSpecializationName(doctor.getSpecializations().getName());
                doctorDTO.setLicenseNumber(doctor.getLicenseNumber());
                doctorDTO.setPhoneNumber(doctor.getPhoneNumber());
                doctorDTO.setEmail(doctor.getEmail());
                doctorDTO.setStatus(doctor.getStatus());
                doctorDTOS.add(doctorDTO);
            }
            return doctorDTOS;
        }catch (Exception e){
            log.error("Error occurred while fetching all doctors", e);
            throw e;
        }
    }

    @Override
    public void updateDoctor(DoctorDTO doctorDTO) {
        log.info("Update Doctor {}", doctorDTO);
        try {
            Optional<Doctor> doctor = doctorRepository.findById(doctorDTO.getDoctorId());

            Specializations specialization = specializationsRepository.findById(doctorDTO.getSpecializationId())
                    .orElseThrow(() ->
                            new RuntimeException("Specialization not found with ID: " + doctorDTO.getSpecializationId()));

            if (doctor.isEmpty())
                throw new RuntimeException("Doctor not found with ID: " + doctorDTO.getDoctorId());

            Doctor doctorUpdate = doctor.get();
            doctorUpdate.setFirstName(doctorDTO.getFirstName());
            doctorUpdate.setLastName(doctorDTO.getLastName());
            doctorUpdate.setSpecializations(specialization);
            doctorUpdate.setLicenseNumber(doctorDTO.getLicenseNumber());
            doctorUpdate.setPhoneNumber(doctorDTO.getPhoneNumber());
            doctorUpdate.setEmail(doctorDTO.getEmail());
            doctorUpdate.setStatus(Status.ACTIVE);
            doctorRepository.save(doctorUpdate);
        }catch (Exception e){
            log.error("Doctor could not be updated", e);
            throw e;
        }
    }
}

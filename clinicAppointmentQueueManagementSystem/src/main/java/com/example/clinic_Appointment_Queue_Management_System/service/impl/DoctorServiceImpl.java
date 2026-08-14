package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public void addDoctor(DoctorDTO doctorDTO) {
        log.info("Doctor added to the queue");
        try {

            User user = userRepository.findById(doctorDTO.getUserId())
                    .orElseThrow(() ->
                            new RuntimeException("User not found with ID: " + doctorDTO.getUserId()));

            long count = doctorRepository.count();
            String generatedId = String.format("D%03d", count + 1);

            Doctor doctor = new Doctor();
            doctor.setDoctorId(generatedId);
            doctor.setUser(user);
            doctor.setFirstName(doctorDTO.getFirstName());
            doctor.setLastName(doctorDTO.getLastName());
            doctor.setSpecialization(doctorDTO.getSpecialization());
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
            doctor.setPhoneNumber(doctorDTO.getPhoneNumber());
            doctor.setStatus(Status.ACTIVE);

            doctorRepository.save(doctor);
        }catch (Exception e){
            log.error("Doctor could not be added to the queue");
            throw e;
        }
    }
}

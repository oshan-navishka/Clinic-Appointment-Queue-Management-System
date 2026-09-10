package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorCardDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;

import java.util.List;

public interface DoctorService {
    void addDoctor(DoctorDTO doctorDTO);

    List<DoctorDTO> getAllDoctors();

    void updateDoctor(DoctorDTO doctorDTO);

    DoctorDTO getByUserId(String userId);

    List<DoctorCardDTO> getDoctorsWithAvailabilityAndFee();
}

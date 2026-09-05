package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;

import java.util.List;

public interface DoctorService {
    void addDoctor(DoctorDTO doctorDTO);

    List<DoctorDTO> getAllDoctors();
}

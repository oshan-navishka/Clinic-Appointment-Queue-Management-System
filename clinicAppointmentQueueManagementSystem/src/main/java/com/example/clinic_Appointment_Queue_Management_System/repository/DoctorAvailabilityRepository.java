package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, String> {

    List<DoctorAvailability> findByDoctor_DoctorIdAndAvailableTrue(String doctorId);

    List<DoctorAvailability> findByDoctor_DoctorId(String doctorId);
}

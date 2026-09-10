package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,String> {

    Optional<Doctor> findByUser_UserId(String userId);

}

package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.AppointmentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentFeeRepository extends JpaRepository<AppointmentFee, String> {

    Optional<AppointmentFee> findByDoctor_DoctorId(String doctorId);
}

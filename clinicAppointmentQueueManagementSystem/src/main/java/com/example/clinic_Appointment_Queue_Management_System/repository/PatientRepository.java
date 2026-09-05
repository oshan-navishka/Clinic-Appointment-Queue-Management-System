package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient,String> {

    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrContactContaining(
            String firstName, String lastName, String contact
    );

}

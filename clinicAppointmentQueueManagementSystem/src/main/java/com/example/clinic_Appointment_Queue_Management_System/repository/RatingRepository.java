package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findByDoctor_DoctorIdOrderByRatedAtDesc(String doctorId);

    List<Rating> findByPatient_PatientId(String patientId);

    Optional<Rating> findByAppointment_AppointmentId(String appointmentId);

    boolean existsByAppointment_AppointmentId(String appointmentId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.doctor.doctorId = :doctorId")
    Double findAverageRatingByDoctorId(@Param("doctorId") String doctorId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.doctor.doctorId = :doctorId")
    long countByDoctorId(@Param("doctorId") String doctorId);
}

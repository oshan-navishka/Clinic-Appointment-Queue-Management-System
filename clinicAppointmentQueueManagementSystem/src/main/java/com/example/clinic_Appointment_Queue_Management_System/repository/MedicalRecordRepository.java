package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {

    @Query("SELECT r FROM MedicalRecord r JOIN FETCH r.patient JOIN FETCH r.doctor " +
           "WHERE r.patient.patientId = :patientId ORDER BY r.visitDate DESC")
    List<MedicalRecord> findByPatientId(@Param("patientId") String patientId);

    @Query("SELECT r FROM MedicalRecord r JOIN FETCH r.patient JOIN FETCH r.doctor " +
           "WHERE r.patient.user.userId = :userId ORDER BY r.visitDate DESC")
    List<MedicalRecord> findByUserId(@Param("userId") String userId);

    Optional<MedicalRecord> findByAppointment_AppointmentId(String appointmentId);

    @Query("SELECT r FROM MedicalRecord r JOIN FETCH r.patient JOIN FETCH r.doctor " +
           "WHERE r.doctor.doctorId = :doctorId ORDER BY r.visitDate DESC")
    List<MedicalRecord> findByDoctorId(@Param("doctorId") String doctorId);
}

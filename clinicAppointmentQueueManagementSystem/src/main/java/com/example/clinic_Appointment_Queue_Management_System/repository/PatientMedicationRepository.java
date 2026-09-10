package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.PatientMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientMedicationRepository extends JpaRepository<PatientMedication, String> {

    @Query("SELECT m FROM PatientMedication m WHERE m.patient.patientId = :patientId ORDER BY m.startDate DESC")
    List<PatientMedication> findByPatientId(@Param("patientId") String patientId);

    @Query("SELECT m FROM PatientMedication m JOIN FETCH m.patient p JOIN FETCH p.user WHERE p.user.userId = :userId ORDER BY m.startDate DESC")
    List<PatientMedication> findByUserId(@Param("userId") String userId);

    @Query("SELECT m FROM PatientMedication m WHERE m.prescription.appointment.appointmentId = :appointmentId")
    List<PatientMedication> findByAppointmentId(@Param("appointmentId") String appointmentId);

    List<PatientMedication> findByPatient_PatientIdAndStatus(String patientId, String status);

    @Query("SELECT m FROM PatientMedication m JOIN FETCH m.patient WHERE m.status = 'PENDING_APPROVAL' ORDER BY m.startDate DESC")
    List<PatientMedication> findAllPendingApproval();

    @Query("SELECT m FROM PatientMedication m JOIN FETCH m.patient p JOIN FETCH p.user u WHERE u.userId = :userId AND m.status <> 'PENDING_APPROVAL' ORDER BY m.startDate DESC")
    List<PatientMedication> findApprovedByUserId(@Param("userId") String userId);
}

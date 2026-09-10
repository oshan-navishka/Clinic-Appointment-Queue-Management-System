package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.Appointments;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.AppointmentState;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, String> {

    @Query("SELECT a FROM Appointments a JOIN FETCH a.patient JOIN FETCH a.doctor")
    List<Appointments> findAllWithDetails();

    @Query("SELECT a FROM Appointments a JOIN FETCH a.patient JOIN FETCH a.doctor " +
            "WHERE a.doctor.doctorId = :doctorId AND a.appointmentDate BETWEEN :start AND :end " +
            "ORDER BY a.appointmentDate, a.appointmentTime")
    List<Appointments> findDoctorWeek(@Param("doctorId") String doctorId,
                                      @Param("start") LocalDate start,
                                      @Param("end") LocalDate end);

    @Query("SELECT a FROM Appointments a JOIN FETCH a.patient JOIN FETCH a.doctor " +
            "WHERE a.patient.patientId = :patientId ORDER BY a.appointmentDate DESC")
    List<Appointments> findByPatientId(@Param("patientId") String patientId);

    @Query("SELECT a FROM Appointments a JOIN FETCH a.patient JOIN FETCH a.doctor " +
            "WHERE a.doctor.doctorId = :doctorId ORDER BY a.appointmentDate DESC")
    List<Appointments> findByDoctorId(@Param("doctorId") String doctorId);

    long countByDoctor_DoctorIdAndAppointmentDate(String doctorId, LocalDate appointmentDate);

    long countByAppointmentState(AppointmentState appointmentState);

    long countByPaymentStatus(PaymentStatus paymentStatus);
}

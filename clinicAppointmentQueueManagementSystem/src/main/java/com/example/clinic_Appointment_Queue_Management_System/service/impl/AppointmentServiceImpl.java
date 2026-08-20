package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Appointments;
import com.example.clinic_Appointment_Queue_Management_System.entity.ClinicBranches;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.ClinicBranchesRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicBranchesRepository clinicBranchesRepository;

    @Override
    public void addAppointment(AppointmentDTO appointmentDTO) {
        log.info("addAppointment");
        try {
            long count = appointmentRepository.count();
            String generatedId = String.format("A%03d", count + 1);

            Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                    .orElseThrow(() ->
                            new RuntimeException("Patient not found with ID: " + appointmentDTO.getPatientId()));

            Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                    .orElseThrow(() ->
                            new RuntimeException("Doctor not found with ID: " + appointmentDTO.getDoctorId()));

            ClinicBranches clinicBranches = clinicBranchesRepository.findById(appointmentDTO.getBranchId())
                    .orElseThrow(() ->
                            new RuntimeException("Clinic Branch not found with ID: " + appointmentDTO.getBranchId()));

            Appointments appointments = new Appointments();
            appointments.setAppointmentId(generatedId);
            appointments.setPatient(patient);
            appointments.setDoctor(doctor);
            appointments.setClinicBranches(clinicBranches);
            appointments.setAppointmentDate(appointmentDTO.getAppointmentDate());
            appointments.setAppointmentTime(appointmentDTO.getAppointmentTime());
            appointments.setAppointmentNumber(appointmentDTO.getAppointmentNumber());
            appointments.setStatus(Status.ACTIVE);
        }catch (Exception e){
            log.error("Appointment could not be added to the queue");
            throw e;
        }
    }
}

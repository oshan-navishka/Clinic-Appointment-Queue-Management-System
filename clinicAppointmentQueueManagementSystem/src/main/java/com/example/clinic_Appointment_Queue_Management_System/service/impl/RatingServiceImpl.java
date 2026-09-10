package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.RatingDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Appointments;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.Rating;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.RatingRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public void saveRating(RatingDTO dto) {
        if (dto.getStars() == null || dto.getStars() < 1 || dto.getStars() > 5) {
            throw new RuntimeException("Stars must be between 1 and 5");
        }
        if (ratingRepository.existsByAppointment_AppointmentId(dto.getAppointmentId())) {
            throw new RuntimeException("You have already rated this appointment");
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Appointments appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        long count = ratingRepository.count();
        String id = String.format("R%03d", count + 1);

        Rating rating = new Rating();
        rating.setRatingId(id);
        rating.setDoctor(doctor);
        rating.setPatient(patient);
        rating.setAppointment(appointment);
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());
        rating.setRatedAt(LocalDateTime.now());

        ratingRepository.save(rating);
        log.info("Rating {} saved for doctor {}", id, doctor.getDoctorId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RatingDTO> getDoctorRatings(String doctorId) {
        return ratingRepository.findByDoctor_DoctorIdOrderByRatedAtDesc(doctorId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RatingDTO getDoctorRatingSummary(String doctorId) {
        Double avg = ratingRepository.findAverageRatingByDoctorId(doctorId);
        long total = ratingRepository.countByDoctorId(doctorId);
        RatingDTO summary = new RatingDTO();
        summary.setDoctorId(doctorId);
        summary.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        summary.setTotalRatings(total);
        return summary;
    }

    @Override
    public boolean hasRated(String appointmentId) {
        return ratingRepository.existsByAppointment_AppointmentId(appointmentId);
    }

    private RatingDTO toDto(Rating r) {
        RatingDTO dto = new RatingDTO();
        dto.setRatingId(r.getRatingId());
        dto.setDoctorId(r.getDoctor().getDoctorId());
        dto.setDoctorName(r.getDoctor().getFirstName() + " " + r.getDoctor().getLastName());
        dto.setPatientId(r.getPatient().getPatientId());
        dto.setPatientName(r.getPatient().getFirstName() + " " + r.getPatient().getLastName());
        dto.setAppointmentId(r.getAppointment() != null ? r.getAppointment().getAppointmentId() : null);
        dto.setStars(r.getStars());
        dto.setComment(r.getComment());
        dto.setRatedAt(r.getRatedAt());
        return dto;
    }
}

package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.RatingDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Appointments;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.Patient;
import com.example.clinic_Appointment_Queue_Management_System.entity.Rating;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
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

    private final RatingRepository      ratingRepository;
    private final DoctorRepository      doctorRepository;
    private final PatientRepository     patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public void saveRating(RatingDTO dto) {
        log.info("Saving rating for appointment {}", dto.getAppointmentId());
        try {
            if (dto.getStars() == null || dto.getStars() < 1 || dto.getStars() > 5) {
                throw new CustomException(400, "Stars must be between 1 and 5");
            }
            if (ratingRepository.existsByAppointment_AppointmentId(dto.getAppointmentId())) {
                throw new CustomException(409, "You have already rated this appointment");
            }

            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new CustomException(404, "Doctor not found"));
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new CustomException(404, "Patient not found"));
            Appointments appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new CustomException(404, "Appointment not found"));

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
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving rating", e);
            throw new CustomException(500, "Error occurred while saving rating");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RatingDTO> getDoctorRatings(String doctorId) {
        try {
            return ratingRepository.findByDoctor_DoctorIdOrderByRatedAtDesc(doctorId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching ratings for doctor {}", doctorId, e);
            throw new CustomException(500, "Error occurred while fetching ratings");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RatingDTO getDoctorRatingSummary(String doctorId) {
        try {
            Double avg   = ratingRepository.findAverageRatingByDoctorId(doctorId);
            long   total = ratingRepository.countByDoctorId(doctorId);
            RatingDTO summary = new RatingDTO();
            summary.setDoctorId(doctorId);
            summary.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
            summary.setTotalRatings(total);
            return summary;
        } catch (Exception e) {
            log.error("Error fetching rating summary for doctor {}", doctorId, e);
            throw new CustomException(500, "Error occurred while fetching rating summary");
        }
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

package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.RatingDTO;

import java.util.List;

public interface RatingService {
    void saveRating(RatingDTO ratingDTO);
    List<RatingDTO> getDoctorRatings(String doctorId);
    RatingDTO getDoctorRatingSummary(String doctorId);
    boolean hasRated(String appointmentId);
}

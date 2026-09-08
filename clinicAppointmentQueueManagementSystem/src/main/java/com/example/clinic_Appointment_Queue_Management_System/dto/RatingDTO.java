package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private String ratingId;
    private String doctorId;
    private String doctorName;
    private String patientId;
    private String patientName;
    private String appointmentId;
    private Integer stars;
    private String comment;
    private LocalDateTime ratedAt;
    private Double averageRating;
    private Long totalRatings;
}

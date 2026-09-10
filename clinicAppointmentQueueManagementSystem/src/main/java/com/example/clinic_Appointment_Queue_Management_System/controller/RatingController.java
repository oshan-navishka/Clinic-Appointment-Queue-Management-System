package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.RatingDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveRating(@RequestBody RatingDTO ratingDTO) {
        log.info("Saving rating for appointment {}", ratingDTO.getAppointmentId());
        ratingService.saveRating(ratingDTO);
        return new CommonResponse(0, "Rating submitted successfully");
    }

    @GetMapping(value = "/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDoctorRatings(@PathVariable String doctorId) {
        return new CommonResponse(0, ratingService.getDoctorRatings(doctorId), "Ratings loaded");
    }

    @GetMapping(value = "/doctor/{doctorId}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDoctorRatingSummary(@PathVariable String doctorId) {
        return new CommonResponse(0, ratingService.getDoctorRatingSummary(doctorId), "Rating summary loaded");
    }

    @GetMapping(value = "/hasRated/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse hasRated(@PathVariable String appointmentId) {
        return new CommonResponse(0, ratingService.hasRated(appointmentId), "Checked");
    }
}

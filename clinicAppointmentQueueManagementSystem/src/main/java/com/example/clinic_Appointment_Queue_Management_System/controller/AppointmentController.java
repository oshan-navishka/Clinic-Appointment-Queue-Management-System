package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/appointments")
@Service
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping(value = "/addAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse AddAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        log.info("Adding appointment: {}", appointmentDTO);
        appointmentService.addAppointment(appointmentDTO);
        return new CommonResponse(0, "Appointment added successfully");
    }
}

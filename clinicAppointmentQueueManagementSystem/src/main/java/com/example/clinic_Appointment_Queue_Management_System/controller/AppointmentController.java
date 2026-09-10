package com.example.clinic_Appointment_Queue_Management_System.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@Slf4j
@RequiredArgsConstructor
public class AppointmentController {
//    private final AppointmentService appointmentService;
//
//    @PostMapping(value = "/addAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
//    public CommonResponse AddAppointment(@RequestBody AppointmentDTO appointmentDTO) {
//        log.info("Adding appointment: {}", appointmentDTO);
//        appointmentService.addAppointment(appointmentDTO);
//        return new CommonResponse(0, "Appointment added successfully");
//    }
}

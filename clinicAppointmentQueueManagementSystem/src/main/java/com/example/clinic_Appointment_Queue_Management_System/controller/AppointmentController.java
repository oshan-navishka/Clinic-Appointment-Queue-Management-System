package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@Slf4j
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping(value = "/addAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse addAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        log.info("Admin adding appointment: {}", appointmentDTO);
        appointmentService.addAppointment(appointmentDTO);
        return new CommonResponse(0, "Appointment added successfully");
    }

    @PostMapping(value = "/bookOnline", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse bookOnline(@RequestBody AppointmentDTO appointmentDTO) {
        log.info("Online booking: {}", appointmentDTO);
        appointmentService.bookOnline(appointmentDTO);
        return new CommonResponse(0, "Appointment booked successfully. Please complete payment.");
    }

    @PostMapping(value = "/pay/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse payAppointment(@PathVariable String appointmentId, @RequestParam String userId) {
        log.info("Paying appointment {} by user {}", appointmentId, userId);
        appointmentService.payAppointment(appointmentId, userId);
        return new CommonResponse(0, "Payment completed successfully");
    }

    @PostMapping(value = "/adminConfirmPayment/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse adminConfirmPayment(@PathVariable String appointmentId) {
        log.info("Admin confirming payment for appointment {}", appointmentId);
        appointmentService.adminConfirmPayment(appointmentId);
        return new CommonResponse(0, "Payment confirmed by admin");
    }

    @PutMapping(value = "/markChecked/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse markChecked(@PathVariable String appointmentId, @RequestParam String userId) {
        log.info("Marking appointment {} checked by doctor user {}", appointmentId, userId);
        appointmentService.markChecked(appointmentId, userId);
        return new CommonResponse(0, "Patient marked as checked");
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllAppointments() {
        return new CommonResponse(0, appointmentService.getAllAppointments(), "Appointments fetched successfully");
    }

    @GetMapping(value = "/doctorWeek", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDoctorWeek(@RequestParam String userId) {
        return new CommonResponse(0, appointmentService.getDoctorWeekAppointments(userId), "This week's patients loaded");
    }

    @GetMapping(value = "/doctorAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDoctorAll(@RequestParam String userId) {
        return new CommonResponse(0, appointmentService.getDoctorAllAppointments(userId), "All appointments for doctor loaded");
    }

    @GetMapping(value = "/myAppointments", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMyAppointments(@RequestParam String userId) {
        return new CommonResponse(0, appointmentService.getMyAppointments(userId), "Your appointments loaded");
    }

    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOverview() {
        return new CommonResponse(0, appointmentService.getAdminOverview(), "Overview loaded");
    }

    @PutMapping(value = "/cancel/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse cancelAppointment(@PathVariable String appointmentId,
                                            @RequestParam String userId,
                                            @RequestParam(required = false) String reason) {
        log.info("Patient {} cancelling appointment {}", userId, appointmentId);
        appointmentService.cancelAppointment(appointmentId, userId, reason);
        return new CommonResponse(0, "Appointment cancelled successfully");
    }

    @PutMapping(value = "/adminCancel/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse adminCancelAppointment(@PathVariable String appointmentId,
                                                 @RequestParam(required = false) String reason) {
        log.info("Admin cancelling appointment {}", appointmentId);
        appointmentService.adminCancelAppointment(appointmentId, reason);
        return new CommonResponse(0, "Appointment cancelled by admin");
    }
}

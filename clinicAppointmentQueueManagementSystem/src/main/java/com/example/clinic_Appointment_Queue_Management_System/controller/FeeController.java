package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentFeeDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.entity.AppointmentFee;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentFeeRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@Slf4j
public class FeeController {

    private final AppointmentFeeRepository feeRepository;
    private final DoctorRepository         doctorRepository;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveFee(@RequestBody AppointmentFeeDTO dto) {
        log.info("Saving fee for doctor {}", dto.getDoctorId());
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + dto.getDoctorId()));

        AppointmentFee fee = feeRepository.findByDoctor_DoctorId(dto.getDoctorId())
                .orElseGet(() -> {
                    AppointmentFee f = new AppointmentFee();
                    long count = feeRepository.count();
                    f.setFeeId(String.format("FEE%03d", count + 1));
                    f.setDoctor(doctor);
                    return f;
                });

        fee.setConsultationFee(dto.getConsultationFee());
        fee.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "LKR");
        fee.setNotes(dto.getNotes());
        feeRepository.save(fee);

        return new CommonResponse(0, "Fee saved successfully");
    }

    @GetMapping(value = "/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByDoctor(@PathVariable String doctorId) {
        return feeRepository.findByDoctor_DoctorId(doctorId)
                .map(fee -> {
                    AppointmentFeeDTO dto = new AppointmentFeeDTO();
                    dto.setFeeId(fee.getFeeId());
                    dto.setDoctorId(doctorId);
                    dto.setConsultationFee(fee.getConsultationFee());
                    dto.setCurrency(fee.getCurrency());
                    dto.setNotes(fee.getNotes());
                    return new CommonResponse(0, dto, "Fee loaded");
                })
                .orElse(new CommonResponse(0, null, "No custom fee set — default LKR 2500 applies"));
    }
}

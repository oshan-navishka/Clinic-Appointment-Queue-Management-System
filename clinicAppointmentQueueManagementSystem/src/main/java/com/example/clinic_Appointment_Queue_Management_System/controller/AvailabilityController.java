package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.DoctorAvailabilityDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.ClinicBranches;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.DoctorAvailability;
import com.example.clinic_Appointment_Queue_Management_System.repository.ClinicBranchesRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorAvailabilityRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Slf4j
public class AvailabilityController {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository             doctorRepository;
    private final ClinicBranchesRepository     branchRepository;

    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveAvailability(@RequestBody DoctorAvailabilityDTO dto) {
        log.info("Saving availability for doctor {} at branch {}", dto.getDoctorId(), dto.getBranchId());

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + dto.getDoctorId()));

        long count = availabilityRepository.count();
        String id  = String.format("AV%03d", count + 1);

        DoctorAvailability av = new DoctorAvailability();
        av.setAvailabilityId(id);
        av.setDoctor(doctor);
        av.setDayOfWeek(dto.getDayOfWeek());
        av.setStartTime(dto.getStartTime());
        av.setEndTime(dto.getEndTime());
        av.setMaxSlots(dto.getMaxSlots() != null ? dto.getMaxSlots() : 20);
        av.setAvailable(true);

        if (dto.getBranchId() != null && !dto.getBranchId().isBlank()) {
            ClinicBranches branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found: " + dto.getBranchId()));
            av.setBranch(branch);
        }

        availabilityRepository.save(av);
        return new CommonResponse(0, "Availability slot saved successfully");
    }

    @GetMapping(value = "/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getByDoctor(@PathVariable String doctorId) {
        List<DoctorAvailabilityDTO> list = availabilityRepository
                .findByDoctor_DoctorId(doctorId)
                .stream().map(this::toDto).collect(Collectors.toList());
        return new CommonResponse(0, list, "Availability loaded");
    }

    @DeleteMapping(value = "/{availabilityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteSlot(@PathVariable String availabilityId) {
        availabilityRepository.deleteById(availabilityId);
        return new CommonResponse(0, "Slot deleted");
    }

    @PutMapping(value = "/{availabilityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateSlot(@PathVariable String availabilityId,
                                     @RequestBody DoctorAvailabilityDTO dto) {
        log.info("Updating availability slot {}", availabilityId);
        DoctorAvailability av = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Slot not found: " + availabilityId));
        av.setDayOfWeek(dto.getDayOfWeek());
        av.setStartTime(dto.getStartTime());
        av.setEndTime(dto.getEndTime());
        av.setMaxSlots(dto.getMaxSlots() != null ? dto.getMaxSlots() : av.getMaxSlots());
        av.setAvailable(dto.isAvailable());
        if (dto.getBranchId() != null && !dto.getBranchId().isBlank()) {
            ClinicBranches branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found: " + dto.getBranchId()));
            av.setBranch(branch);
        } else {
            av.setBranch(null);
        }
        availabilityRepository.save(av);
        return new CommonResponse(0, "Slot updated successfully");
    }

    private DoctorAvailabilityDTO toDto(DoctorAvailability av) {
        DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO();
        dto.setAvailabilityId(av.getAvailabilityId());
        dto.setDoctorId(av.getDoctor().getDoctorId());
        dto.setDayOfWeek(av.getDayOfWeek());
        dto.setStartTime(av.getStartTime());
        dto.setEndTime(av.getEndTime());
        dto.setMaxSlots(av.getMaxSlots());
        dto.setAvailable(av.isAvailable());
        if (av.getBranch() != null) {
            dto.setBranchId(av.getBranch().getBranchId());
            dto.setBranchName(av.getBranch().getBranchName());
        }
        return dto;
    }
}

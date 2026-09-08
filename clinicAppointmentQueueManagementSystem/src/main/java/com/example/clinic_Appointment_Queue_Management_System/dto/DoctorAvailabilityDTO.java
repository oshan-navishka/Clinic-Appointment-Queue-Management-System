package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityDTO {
    private String availabilityId;
    private String doctorId;
    private String branchId;
    private String branchName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private Integer maxSlots;
    private boolean available;
}

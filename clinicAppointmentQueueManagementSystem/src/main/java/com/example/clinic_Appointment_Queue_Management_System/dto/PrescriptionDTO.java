package com.example.clinic_Appointment_Queue_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDTO {
    private String prescriptionId;
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDate issuedDate;
    private LocalDate validUntil;
    private String instructions;

    private List<MedicineItemDTO> medicines;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedicineItemDTO {
        private String medicineName;
        private String dosage;
        private String frequency;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}

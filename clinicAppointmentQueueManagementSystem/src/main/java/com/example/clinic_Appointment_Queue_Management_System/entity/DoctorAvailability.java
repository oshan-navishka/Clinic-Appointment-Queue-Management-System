package com.example.clinic_Appointment_Queue_Management_System.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
public class DoctorAvailability {
    @Id
    private String availabilityId;

    @ManyToOne
    @JoinColumn(name = "doctorId", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "branchId")
    private ClinicBranches branch;

    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private int maxSlots;
    private boolean available;
}

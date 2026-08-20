package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DoctorSchedules {
    @Id
    private String scheduleId;

    @ManyToOne
    @JoinColumn(name = "doctorId", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "branchId", nullable = false)
    private ClinicBranches clinicBranches;

    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private int maxAppointments;

    @Enumerated(EnumType.STRING)
    private Status status;
}

package com.example.clinic_Appointment_Queue_Management_System.entity;

import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ClinicBranches {
    @Id
    private String branchId;
    private String branchName;
    private String address;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "clinicBranches", cascade = CascadeType.ALL)
    private List<DoctorSchedules> doctorSchedules;

    @OneToMany(mappedBy = "clinicBranches", cascade = CascadeType.ALL)
    private List<Appointments> appointmentsList;
}
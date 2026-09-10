package com.example.clinic_Appointment_Queue_Management_System;

import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean hasSuperAdmin = userRepository.findAll().stream()
                .anyMatch(user -> user.getUserRole() == UserRole.SUPER_ADMIN);
        if (hasSuperAdmin) {
            return;
        }

        User superAdmin = new User();
        superAdmin.setUserId("U000");
        superAdmin.setUsername("superadmin");
        superAdmin.setPassword(passwordEncoder.encode("SuperAdmin@123"));
        superAdmin.setUserEmail("superadmin@clinic.local");
        superAdmin.setUserRole(UserRole.SUPER_ADMIN);
        superAdmin.setStatus(Status.ACTIVE);
        userRepository.save(superAdmin);
    }
}

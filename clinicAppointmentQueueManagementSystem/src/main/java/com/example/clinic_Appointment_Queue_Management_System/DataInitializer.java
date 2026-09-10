package com.example.clinic_Appointment_Queue_Management_System;

import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {

        try {
            jdbcTemplate.execute(
                "ALTER TABLE appointments MODIFY COLUMN appointment_state VARCHAR(20)"
            );
            log.info("appointment_state column migrated to VARCHAR(20)");
        } catch (Exception e) {
            log.debug("appointment_state migration skipped: {}", e.getMessage());
        }

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
        log.info("Super Admin account created");
    }
}

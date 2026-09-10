package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.UserDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.EmailService;
import com.example.clinic_Appointment_Queue_Management_System.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService    emailService;

    @Override
    public void saveUser(UserDTO userDTO) {
        log.info("Saving user {}", userDTO);
        try {
            if (userRepository.existsByUsername(userDTO.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            if (userDTO.getUserEmail() != null && userRepository.existsByUserEmail(userDTO.getUserEmail())) {
                throw new RuntimeException("Email already exists");
            }

            long count = userRepository.count();
            String generatedId = String.format("U%03d", count + 1);

            User user = new User();
            user.setUserId(generatedId);
            user.setUsername(userDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setUserEmail(userDTO.getUserEmail());
            user.setUserRole(userDTO.getUserRole());
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);

            try {
                if (userDTO.getUserEmail() != null && !userDTO.getUserEmail().isBlank()
                        && userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
                    String role = userDTO.getUserRole() != null
                            ? userDTO.getUserRole().name() : "ADMIN";
                    emailService.sendWelcomeEmail(
                            userDTO.getUserEmail(),
                            userDTO.getUsername(),
                            userDTO.getUsername(),
                            userDTO.getPassword(),
                            role
                    );
                }
            } catch (Exception ex) {
                log.warn("Could not send welcome email for user {}: {}", userDTO.getUsername(), ex.getMessage());
            }

        } catch (Exception e) {
            log.error("Error saving user {}", userDTO);
            throw e;
        }
    }

    @Override
    public UserDTO getUserDetails(String username, String password) {
        log.info("Fetching user {}", username);
        try{
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByUserEmail(username);
            }
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User userDetails = userOptional.get();

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            log.info("Fetching user details {}", username);

            return new UserDTO(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getUserEmail(),
                    userDetails.getUserRole(),
                    userDetails.getStatus()
            );
        }catch (Exception e){
            log.error("Error fetching user details {}", username);
            throw e;
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<UserDTO> userDTOS = new ArrayList<>();
            List<User> users = userRepository.findAll();

            for (User user : users) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setUsername(user.getUsername());
                userDTO.setPassword(user.getPassword());
                userDTO.setUserEmail(user.getUserEmail());
                userDTO.setUserRole(user.getUserRole());
                userDTO.setStatus(user.getStatus());
                userDTOS.add(userDTO);
            }
            return userDTOS;
        } catch (Exception e) {
            log.error("Error fetching all users");
            throw e;
        }
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        log.info("Updating user {}", userDTO);
        try{
            Optional<User> userOptional = userRepository.findById(userDTO.getUserId());
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            User user =  userOptional.get();
            user.setUsername(userDTO.getUsername());
            user.setUserEmail(userDTO.getUserEmail());
            user.setUserRole(userDTO.getUserRole());
            userRepository.save(user);
        }catch (Exception e){
            log.error("Error fetching user {}", userDTO);
            throw e;
        }
    }
}

package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.ChangeCredentialsDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
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
                throw new CustomException(409, "Username already exists");
            }
            if (userDTO.getUserEmail() != null && userRepository.existsByUserEmail(userDTO.getUserEmail())) {
                throw new CustomException(409, "Email already exists");
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
                    String role = userDTO.getUserRole() != null ? userDTO.getUserRole().name() : "ADMIN";
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

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving user", e);
            throw new CustomException(500, "Error occurred while saving user");
        }
    }

    @Override
    public UserDTO getUserDetails(String username, String password) {
        log.info("Fetching user {}", username);
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByUserEmail(username);
            }
            if (userOptional.isEmpty()) {
                throw new CustomException(404, "User not found");
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new CustomException(401, "Wrong password");
            }

            return new UserDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getUserEmail(),
                    user.getUserRole(),
                    user.getStatus()
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user details for {}", username, e);
            throw new CustomException(500, "Error occurred while fetching user details");
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<UserDTO> userDTOS = new ArrayList<>();
            for (User user : userRepository.findAll()) {
                UserDTO dto = new UserDTO();
                dto.setUserId(user.getUserId());
                dto.setUsername(user.getUsername());
                dto.setPassword(user.getPassword());
                dto.setUserEmail(user.getUserEmail());
                dto.setUserRole(user.getUserRole());
                dto.setStatus(user.getStatus());
                userDTOS.add(dto);
            }
            return userDTOS;
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            throw new CustomException(500, "Error occurred while fetching users");
        }
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        log.info("Updating user {}", userDTO.getUserId());
        try {
            User user = userRepository.findById(userDTO.getUserId())
                    .orElseThrow(() -> new CustomException(404, "User not found"));

            user.setUsername(userDTO.getUsername());
            user.setUserEmail(userDTO.getUserEmail());
            user.setUserRole(userDTO.getUserRole());
            userRepository.save(user);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating user {}", userDTO.getUserId(), e);
            throw new CustomException(500, "Error occurred while updating user");
        }
    }

    @Override
    public void deleteUser(String userId) {
        log.info("Deleting user {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(404, "User not found"));

            if (user.getUserRole() == UserRole.SUPER_ADMIN) {
                throw new CustomException(403, "Cannot delete a SUPER_ADMIN account");
            }

            userRepository.deleteById(userId);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user {}", userId, e);
            throw new CustomException(500, "Error occurred while deleting user");
        }
    }

    @Override
    public void changeCredentials(ChangeCredentialsDTO dto) {
        log.info("Changing credentials for user {}", dto.getUserId());
        try {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new CustomException(404, "User not found"));

            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new CustomException(401, "Current password is incorrect");
            }

            if (dto.getNewUsername() != null && !dto.getNewUsername().isBlank()) {
                String newUsername = dto.getNewUsername().trim();
                if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                    throw new CustomException(409, "Username already taken");
                }
                user.setUsername(newUsername);
            }

            if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }

            userRepository.save(user);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error changing credentials for user {}", dto.getUserId(), e);
            throw new CustomException(500, "Error occurred while changing credentials");
        }
    }

    @Override
    public void resetPassword(String userId, String newPassword) {
        log.info("Resetting password for user {}", userId);
        try {
            if (newPassword == null || newPassword.isBlank()) {
                throw new CustomException(400, "New password cannot be empty");
            }
            if (newPassword.length() < 6) {
                throw new CustomException(400, "Password must be at least 6 characters");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(404, "User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error resetting password for user {}", userId, e);
            throw new CustomException(500, "Error occurred while resetting password");
        }
    }
}

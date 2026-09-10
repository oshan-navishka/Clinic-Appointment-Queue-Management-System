package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.ChangeCredentialsDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDTO);
    UserDTO getUserDetails(String username, String password);
    List<UserDTO> getAllUsers();
    void updateUser(UserDTO userDTO);
    void deleteUser(String userId);
    void changeCredentials(ChangeCredentialsDTO changeCredentialsDTO);
    void resetPassword(String userId, String newPassword);
}

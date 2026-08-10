package com.example.clinic_Appointment_Queue_Management_System.service;

import com.example.clinic_Appointment_Queue_Management_System.dto.UserDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDataDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDTO);
    UserDTO getUserDetails(String username, String password);
    List<UserDTO> getAllUsers();
}

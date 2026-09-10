package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.AuthDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDataDTO;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.UserRole;
import com.example.clinic_Appointment_Queue_Management_System.security.JwtUtil;
import com.example.clinic_Appointment_Queue_Management_System.service.DoctorService;
import com.example.clinic_Appointment_Queue_Management_System.service.PatientService;
import com.example.clinic_Appointment_Queue_Management_System.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @PostMapping(value = "register", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveUser(@RequestBody UserDTO userDTO){
        userService.saveUser(userDTO);
        return new CommonResponse(0, "User has been saved successfully");
    }

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse login(@RequestBody AuthDTO authDTO){
        UserDTO userDTO = userService.getUserDetails(authDTO.getUsername(), authDTO.getPassword());
        String token = jwtUtil.generateToken(userDTO);

        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setUserId(userDTO.getUserId());
        userDataDTO.setToken(token);
        userDataDTO.setUserRole(userDTO.getUserRole());
        if (userDTO.getUserRole() == UserRole.PATIENT) {
            try {
                userDataDTO.setPatientId(patientService.getByUserId(userDTO.getUserId()).getPatientId());
            } catch (Exception ignored) {
            }
        }
        if (userDTO.getUserRole() == UserRole.DOCTOR) {
            try {
                userDataDTO.setDoctorId(doctorService.getByUserId(userDTO.getUserId()).getDoctorId());
            } catch (Exception ignored) {
            }
        }

        return new CommonResponse(0, userDataDTO, "User has been logged in successfully");

    }

    @GetMapping(value = "allUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllUsers(){
        List<UserDTO> userDTOS = userService.getAllUsers();
        return new CommonResponse(0, userDTOS, "Users fetched successfully");
    }

    @PutMapping(value = "updateUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateUsers(@RequestBody UserDTO userDTO){
        userService.updateUser(userDTO);
        return new CommonResponse(0, "User has been updated successfully");
    }

    @DeleteMapping(value = "deleteUser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return new CommonResponse(0, "User has been deleted successfully");
    }

}

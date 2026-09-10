package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.AuthDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDTO;
import com.example.clinic_Appointment_Queue_Management_System.dto.UserDataDTO;
import com.example.clinic_Appointment_Queue_Management_System.security.JwtUtil;
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

}

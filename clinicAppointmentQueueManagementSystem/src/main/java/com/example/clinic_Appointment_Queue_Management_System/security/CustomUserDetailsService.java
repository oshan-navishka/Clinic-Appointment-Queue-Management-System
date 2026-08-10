package com.example.clinic_Appointment_Queue_Management_System.security;

import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.example.clinic_Appointment_Queue_Management_System.entity.User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty())
            throw new RuntimeException("Sorry no user");


        return User.builder()
                .username(optionalUser.get().getUsername())
                .password(optionalUser.get().getPassword())
                .roles(optionalUser.get().getUserRole().name())
                .build();
    }

}

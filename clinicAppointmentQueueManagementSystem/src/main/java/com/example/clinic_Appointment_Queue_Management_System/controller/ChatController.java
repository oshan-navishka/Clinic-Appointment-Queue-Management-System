package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.ChatRequest;
import com.example.clinic_Appointment_Queue_Management_System.dto.ChatResponse;
import com.example.clinic_Appointment_Queue_Management_System.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("Chat request: {}", request.getMessage());
        return chatService.chat(request);
    }
}

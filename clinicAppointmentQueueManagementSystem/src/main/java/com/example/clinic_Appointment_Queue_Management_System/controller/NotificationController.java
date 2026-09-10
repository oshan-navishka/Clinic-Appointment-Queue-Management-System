package com.example.clinic_Appointment_Queue_Management_System.controller;

import com.example.clinic_Appointment_Queue_Management_System.dto.CommonResponse;
import com.example.clinic_Appointment_Queue_Management_System.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/myNotifications", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getMyNotifications(@RequestParam String userId) {
        return new CommonResponse(0, notificationService.getUserNotifications(userId), "Notifications loaded");
    }

    @GetMapping(value = "/unreadCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getUnreadCount(@RequestParam String userId) {
        return new CommonResponse(0, notificationService.getUnreadCount(userId), "Unread count");
    }

    @PutMapping(value = "/markAllRead", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse markAllRead(@RequestParam String userId) {
        notificationService.markAllRead(userId);
        return new CommonResponse(0, "All notifications marked as read");
    }
}

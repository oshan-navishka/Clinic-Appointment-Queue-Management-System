package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.NotificationDTO;
import com.example.clinic_Appointment_Queue_Management_System.entity.Notification;
import com.example.clinic_Appointment_Queue_Management_System.entity.User;
import com.example.clinic_Appointment_Queue_Management_System.exception.CustomException;
import com.example.clinic_Appointment_Queue_Management_System.repository.NotificationRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.UserRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

    @Override
    @Transactional
    public void createNotification(String userId, String title, String message,
                                   String type, String referenceId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(404, "User not found: " + userId));

            long count = notificationRepository.count();
            String id = String.format("N%03d", count + 1);

            Notification n = new Notification();
            n.setNotificationId(id);
            n.setUser(user);
            n.setTitle(title);
            n.setMessage(message);
            n.setRead(false);
            n.setType(type);
            n.setCreatedAt(LocalDateTime.now());
            n.setReferenceId(referenceId);
            notificationRepository.save(n);
            log.info("Notification {} created for user {}", id, userId);
        } catch (Exception e) {
            log.error("Failed to create notification for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(String userId) {
        try {
            return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                    .stream().map(this::toDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching notifications for user {}", userId, e);
            throw new CustomException(500, "Error occurred while fetching notifications");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        try {
            return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
        } catch (Exception e) {
            log.error("Error fetching unread count for user {}", userId, e);
            throw new CustomException(500, "Error occurred while fetching unread count");
        }
    }

    @Override
    @Transactional
    public void markAllRead(String userId) {
        try {
            List<Notification> unread = notificationRepository.findByUser_UserIdAndIsReadFalse(userId);
            unread.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(unread);
        } catch (Exception e) {
            log.error("Error marking notifications as read for user {}", userId, e);
            throw new CustomException(500, "Error occurred while marking notifications as read");
        }
    }

    private NotificationDTO toDto(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(n.getNotificationId());
        dto.setUserId(n.getUser().getUserId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setType(n.getType());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setReferenceId(n.getReferenceId());
        return dto;
    }
}

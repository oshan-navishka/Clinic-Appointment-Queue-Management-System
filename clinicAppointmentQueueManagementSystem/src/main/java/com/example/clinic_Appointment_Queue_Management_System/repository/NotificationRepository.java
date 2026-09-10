package com.example.clinic_Appointment_Queue_Management_System.repository;

import com.example.clinic_Appointment_Queue_Management_System.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findByUser_UserIdAndIsReadFalse(String userId);

    long countByUser_UserIdAndIsReadFalse(String userId);
}

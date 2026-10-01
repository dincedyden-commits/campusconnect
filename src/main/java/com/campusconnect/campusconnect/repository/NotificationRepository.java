package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Notification; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long>{List<Notification> findByUser_IdOrderByCreatedAtDesc(Long id); long countByUser_IdAndReadFlagFalse(Long id);}
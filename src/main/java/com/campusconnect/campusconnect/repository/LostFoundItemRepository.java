package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.LostFoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface LostFoundItemRepository extends JpaRepository<LostFoundItem,Long>{List<LostFoundItem> findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime now);long deleteByExpiresAtBefore(LocalDateTime now);}

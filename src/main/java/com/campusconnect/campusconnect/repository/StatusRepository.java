package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface StatusRepository extends JpaRepository<Status,Long>{
 List<Status> findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime now);
}

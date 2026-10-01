package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Message; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface MessageRepository extends JpaRepository<Message,Long>{
 List<Message> findBySender_IdOrRecipient_IdOrderByCreatedAtDesc(Long a,Long b);
 List<Message> findBySender_IdAndRecipient_IdOrSender_IdAndRecipient_IdOrderByCreatedAtAsc(Long s,Long r,Long r2,Long s2);
}
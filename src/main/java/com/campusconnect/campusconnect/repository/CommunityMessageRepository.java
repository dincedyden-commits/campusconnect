package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.CommunityMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage,Long>{
 List<CommunityMessage> findByCommunity_IdOrderByCreatedAtAsc(Long communityId);
}

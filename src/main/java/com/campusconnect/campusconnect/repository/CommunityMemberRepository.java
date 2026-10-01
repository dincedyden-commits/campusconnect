package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CommunityMemberRepository extends JpaRepository<CommunityMember,Long>{
 Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId,Long userId);
 List<CommunityMember> findByCommunityIdOrderByJoinedAtAsc(Long communityId);
 boolean existsByCommunityIdAndUserId(Long communityId,Long userId);
}

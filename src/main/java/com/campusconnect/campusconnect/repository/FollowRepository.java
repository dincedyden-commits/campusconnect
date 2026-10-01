package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface FollowRepository extends JpaRepository<Follow,Long>{
 boolean existsByFollower_IdAndFollowing_Id(Long a,Long b);
 Optional<Follow> findByFollower_IdAndFollowing_Id(Long a,Long b);
 long countByFollowing_Id(Long id); long countByFollower_Id(Long id);
 List<Follow> findByFollower_Id(Long id);
}
package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PostLikeRepository extends JpaRepository<PostLike,Long>{
 boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
 Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);
 long countByPost_Id(Long postId); List<PostLike> findByPost_Id(Long postId);
}

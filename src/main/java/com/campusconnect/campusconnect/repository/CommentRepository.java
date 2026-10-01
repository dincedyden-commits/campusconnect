package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Comment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CommentRepository extends JpaRepository<Comment,Long>{List<Comment> findByPost_IdOrderByCreatedAtAsc(Long id); long countByPost_Id(Long id);}
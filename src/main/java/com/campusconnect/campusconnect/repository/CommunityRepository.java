package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CommunityRepository extends JpaRepository<Community,Long>{ List<Community> findAllByOrderByCreatedAtDesc(); }

package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByAuthor_IdOrderByCreatedAtDesc(Long authorId);

    Post findTopByAuthor_IdOrderByIdDesc(Long authorId);

    List<Post> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String content);
}

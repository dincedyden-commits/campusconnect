package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findTop20ByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(String username, String fullName);
}

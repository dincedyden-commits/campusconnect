package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.Block; import org.springframework.data.jpa.repository.JpaRepository;
public interface BlockRepository extends JpaRepository<Block,Long>{ boolean existsByBlocker_IdAndBlocked_Id(Long a,Long b); java.util.Optional<Block> findByBlocker_IdAndBlocked_Id(Long a,Long b); }
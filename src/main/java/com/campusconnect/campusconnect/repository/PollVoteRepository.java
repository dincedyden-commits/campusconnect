package com.campusconnect.campusconnect.repository;
import com.campusconnect.campusconnect.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PollVoteRepository extends JpaRepository<PollVote,Long>{Optional<PollVote> findByPollIdAndUserId(Long pollId,Long userId);}

package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="poll_votes", uniqueConstraints=@UniqueConstraint(name="uk_poll_vote_user", columnNames={"poll_id","user_id"}))
public class PollVote {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="poll_id") private Poll poll;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id") private User user;
    @Column(nullable=false) private int optionIndex;
    @Column(nullable=false, updatable=false) private LocalDateTime votedAt;
    @PrePersist void onCreate(){votedAt=LocalDateTime.now();}
    public void setPoll(Poll v){poll=v;} public Poll getPoll(){return poll;}
    public void setUser(User v){user=v;} public User getUser(){return user;}
    public void setOptionIndex(int v){optionIndex=v;} public int getOptionIndex(){return optionIndex;}
}

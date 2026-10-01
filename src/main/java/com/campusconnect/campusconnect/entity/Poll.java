package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="campus_polls")
public class Poll {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=180) private String question;
    @Column(nullable=false, length=160) private String optionOne;
    @Column(nullable=false, length=160) private String optionTwo;
    @Column(nullable=false) private long votesOne;
    @Column(nullable=false) private long votesTwo;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="created_by_id") private User createdBy;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){createdAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getQuestion(){return question;} public void setQuestion(String v){question=v;}
    public String getOptionOne(){return optionOne;} public void setOptionOne(String v){optionOne=v;}
    public String getOptionTwo(){return optionTwo;} public void setOptionTwo(String v){optionTwo=v;}
    public long getVotesOne(){return votesOne;} public void setVotesOne(long v){votesOne=v;}
    public long getVotesTwo(){return votesTwo;} public void setVotesTwo(long v){votesTwo=v;}
    public User getCreatedBy(){return createdBy;} public void setCreatedBy(User v){createdBy=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

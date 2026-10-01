package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "campus_events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=160) private String name;
    @Column(nullable=false) private LocalDateTime eventDate;
    @Column(nullable=false, length=180) private String location;
    @Column(length=2000) private String description;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="created_by_id") private User createdBy;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void onCreate(){ createdAt=LocalDateTime.now(); }
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
    public LocalDateTime getEventDate(){return eventDate;} public void setEventDate(LocalDateTime v){eventDate=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public User getCreatedBy(){return createdBy;} public void setCreatedBy(User v){createdBy=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}

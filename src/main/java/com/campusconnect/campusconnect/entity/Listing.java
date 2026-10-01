package com.campusconnect.campusconnect.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="marketplace_items")
public class Listing {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String title;
 @Column(nullable=false,length=1000) private String description;
 @Column(nullable=false) private double price;
 @Column(length=60) private String category;
 @Column(length=150) private String location;
 @Column(name="image_url",length=500) private String imageUrl;
 @Column(nullable=false) private boolean sold=false;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="seller_id",nullable=false) private User seller;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
 @PrePersist void onCreate(){createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;} public double getPrice(){return price;} public void setPrice(double v){price=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;} public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;} public boolean isSold(){return sold;} public void setSold(boolean v){sold=v;}
 public User getSeller(){return seller;} public void setSeller(User v){seller=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
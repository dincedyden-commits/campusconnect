package com.campusconnect.campusconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="messages")
public class Message {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sender_id",nullable=false) private User sender;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="recipient_id",nullable=false) private User recipient;

    @Column(nullable=false,length=4000) private String content;
    @Column(name="view_once",nullable=false) private boolean viewOnce;
    @Column(nullable=false) private boolean viewed;
    @Column(name="created_at",nullable=false) private LocalDateTime createdAt;

    @Column(name="product_id") private Long productId;
    @Column(name="product_title",length=255) private String productTitle;
    @Column(name="product_image_url",length=1000) private String productImageUrl;
    @Column(name="product_price") private Double productPrice;

    // Per-user soft deletion. No message row is ever physically deleted.
    @Column(name="deleted_by_user",nullable=false) private boolean deletedByUser;
    @Column(name="deleted_at") private LocalDateTime deletedAt;
    @Column(name="deleted_by_sender",nullable=false) private boolean deletedBySender;
    @Column(name="deleted_at_sender") private LocalDateTime deletedAtSender;
    @Column(name="deleted_by_recipient",nullable=false) private boolean deletedByRecipient;
    @Column(name="deleted_at_recipient") private LocalDateTime deletedAtRecipient;

    @PrePersist void onCreate(){
        if(createdAt==null) createdAt=LocalDateTime.now();
    }

    public Long getId(){return id;}
    public User getSender(){return sender;} public void setSender(User v){sender=v;}
    public User getRecipient(){return recipient;} public void setRecipient(User v){recipient=v;}
    public String getContent(){return content;} public void setContent(String v){content=v;}
    public boolean isViewOnce(){return viewOnce;} public void setViewOnce(boolean v){viewOnce=v;}
    public boolean isViewed(){return viewed;} public void setViewed(boolean v){viewed=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
    public String getProductTitle(){return productTitle;} public void setProductTitle(String v){productTitle=v;}
    public String getProductImageUrl(){return productImageUrl;} public void setProductImageUrl(String v){productImageUrl=v;}
    public Double getProductPrice(){return productPrice;} public void setProductPrice(Double v){productPrice=v;}

    public boolean isDeletedByUser(){return deletedByUser;} public void setDeletedByUser(boolean v){deletedByUser=v;}
    public LocalDateTime getDeletedAt(){return deletedAt;} public void setDeletedAt(LocalDateTime v){deletedAt=v;}
    public boolean isDeletedBySender(){return deletedBySender;} public void setDeletedBySender(boolean v){deletedBySender=v;}
    public LocalDateTime getDeletedAtSender(){return deletedAtSender;} public void setDeletedAtSender(LocalDateTime v){deletedAtSender=v;}
    public boolean isDeletedByRecipient(){return deletedByRecipient;} public void setDeletedByRecipient(boolean v){deletedByRecipient=v;}
    public LocalDateTime getDeletedAtRecipient(){return deletedAtRecipient;} public void setDeletedAtRecipient(LocalDateTime v){deletedAtRecipient=v;}
}

package com.campusconnect.campusconnect.service;

import com.campusconnect.campusconnect.dto.*;
import com.campusconnect.campusconnect.entity.*;
import com.campusconnect.campusconnect.exception.*;
import com.campusconnect.campusconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PostService {
    private final PostRepository posts; private final UserRepository users; private final PostLikeRepository likes;
    private final CommentRepository comments; private final NotificationRepository notifications; private final FollowRepository follows;

    public PostService(PostRepository p, UserRepository u, PostLikeRepository l, CommentRepository c, NotificationRepository n, FollowRepository f){
        posts=p; users=u; likes=l; comments=c; notifications=n; follows=f;
    }

    private PostResponse dto(Post p, Long uid){
        boolean following = uid != null && !uid.equals(p.getAuthor().getId()) && follows.existsByFollower_IdAndFollowing_Id(uid,p.getAuthor().getId());
        return PostResponse.from(p, likes.countByPost_Id(p.getId()), comments.countByPost_Id(p.getId()),
                uid!=null && likes.existsByPost_IdAndUser_Id(p.getId(),uid), following);
    }

    @Transactional
    public PostResponse createPost(PostRequest req, Long uid){
        String content=req.getContent()==null?"":req.getContent().trim();
        String image=req.getImageUrl()==null?"":req.getImageUrl().trim();
        String video=req.getVideoUrl()==null?"":req.getVideoUrl().trim();
        if(content.isEmpty()&&image.isEmpty()&&video.isEmpty()) throw new IllegalArgumentException("Write something or choose an image/video");
        if(!image.isEmpty()&&!video.isEmpty()) throw new IllegalArgumentException("A post can contain one media file at a time");
        User author = users.findById(uid).orElseThrow(()->new UserNotFoundException(uid));
        String mediaUrl=image.isEmpty()?(video.isEmpty()?null:video):image;
        String mediaType=image.isEmpty()?(video.isEmpty()?null:"video/*"):"image/*";

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content.isEmpty()?"(media)":content);
        post.setMediaUrl(mediaUrl);
        post.setMediaType(mediaType);
        post.setViewCount(0);
        Post saved = posts.saveAndFlush(post);
        return dto(saved,uid);
    }

    @Transactional(readOnly=true)
    public List<PostResponse> getFeed(Long uid, String mode){
        var all=posts.findAllByOrderByCreatedAtDesc().stream();
        if("following".equalsIgnoreCase(mode)){
            all=all.filter(p -> p.getAuthor().getId().equals(uid) ||
                    follows.existsByFollower_IdAndFollowing_Id(uid,p.getAuthor().getId()));
        }
        return all.map(p->dto(p,uid)).toList();
    }

    @Transactional(readOnly=true)
    public List<PostResponse> getFeed(Long uid){ return getFeed(uid,"for-you"); }
    @Transactional(readOnly=true) public List<PostResponse> getByUser(Long id,Long uid){if(!users.existsById(id))throw new UserNotFoundException(id);return posts.findByAuthor_IdOrderByCreatedAtDesc(id).stream().map(p->dto(p,uid)).toList();}
    @Transactional(readOnly=true) public List<PostResponse> search(String q,Long uid){if(q==null||q.isBlank())return List.of();return posts.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(q.trim()).stream().map(p->dto(p,uid)).toList();}

    @Transactional public void view(Long id,Long uid){ Post p=posts.findById(id).orElseThrow(()->new PostNotFoundException(id)); p.setViewCount(p.getViewCount()+1); posts.save(p); }

    @Transactional public PostResponse like(Long id,Long uid){
        Post p=posts.findById(id).orElseThrow(()->new PostNotFoundException(id));
        if(!likes.existsByPost_IdAndUser_Id(id,uid)){PostLike l=new PostLike();User actor=users.findById(uid).orElseThrow();l.setPost(p);l.setUser(actor);likes.save(l);
            if(!p.getAuthor().getId().equals(uid)){Notification n=new Notification();n.setUser(p.getAuthor());n.setType("like");n.setMessage("@"+actor.getUsername()+" liked your post");notifications.save(n);}}
        return dto(p,uid);
    }
    @Transactional public PostResponse unlike(Long id,Long uid){Post p=posts.findById(id).orElseThrow(()->new PostNotFoundException(id));likes.findByPost_IdAndUser_Id(id,uid).ifPresent(likes::delete);return dto(p,uid);}
    @Transactional public PostResponse edit(Long id,String content,Long uid){Post p=posts.findById(id).orElseThrow(()->new PostNotFoundException(id));if(!p.getAuthor().getId().equals(uid))throw new IllegalArgumentException("Only the author can edit this post");if(Duration.between(p.getCreatedAt(),LocalDateTime.now()).toMinutes()>20)throw new IllegalArgumentException("Posts can only be edited within 20 minutes"); if(content==null||content.trim().isEmpty())throw new IllegalArgumentException("Post content is required"); if(content.trim().length()>280)throw new IllegalArgumentException("Posts are limited to 280 characters"); p.setContent(content.trim());return dto(posts.save(p),uid);}
    @Transactional public void delete(Long id,Long uid){Post p=posts.findById(id).orElseThrow(()->new PostNotFoundException(id));if(!p.getAuthor().getId().equals(uid))throw new IllegalArgumentException("Only the author can delete this post");comments.findByPost_IdOrderByCreatedAtAsc(id).forEach(comments::delete);likes.findByPost_Id(id).forEach(likes::delete);posts.delete(p);}
}

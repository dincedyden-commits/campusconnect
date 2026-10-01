package com.campusconnect.campusconnect.controller;
import com.campusconnect.campusconnect.dto.*; import com.campusconnect.campusconnect.service.PostService;
import jakarta.servlet.http.HttpSession; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/posts")
public class PostController {
 private final PostService service; public PostController(PostService s){service=s;}
 private Long me(HttpSession s){Object x=s.getAttribute("userId");if(x==null)throw new IllegalArgumentException("Log in first");return ((Number)x).longValue();}
 @GetMapping
 public List<PostResponse> feed(@RequestParam(defaultValue="for-you") String mode,HttpSession s){
     if(!mode.equalsIgnoreCase("for-you")&&!mode.equalsIgnoreCase("following"))
         throw new IllegalArgumentException("Feed mode must be for-you or following");
     return service.getFeed(me(s),mode);
 }
 @PostMapping public ResponseEntity<PostResponse> create(@RequestBody PostRequest r,HttpSession s){return ResponseEntity.status(HttpStatus.CREATED).body(service.createPost(r,me(s)));}
 @GetMapping("/user/{id}") public List<PostResponse> user(@PathVariable Long id,HttpSession s){return service.getByUser(id,me(s));}
 @GetMapping("/search") public List<PostResponse> search(@RequestParam(required=false) String q,HttpSession s){return service.search(q,me(s));}
 @PostMapping("/{id}/like") public PostResponse like(@PathVariable Long id,HttpSession s){return service.like(id,me(s));}
 @PostMapping("/{id}/unlike") public PostResponse unlike(@PathVariable Long id,HttpSession s){return service.unlike(id,me(s));}
 @PutMapping("/{id}") public PostResponse edit(@PathVariable Long id,@RequestBody Map<String,String> b,HttpSession s){return service.edit(id,b.get("content"),me(s));}
 @PostMapping("/{id}/view") public void view(@PathVariable Long id,HttpSession s){service.view(id,me(s));}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id,HttpSession s){service.delete(id,me(s));}
}

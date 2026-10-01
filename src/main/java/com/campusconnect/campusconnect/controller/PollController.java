package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.*;
import com.campusconnect.campusconnect.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/polls")
public class PollController {
    private final PollRepository polls; private final PollVoteRepository votes; private final UserRepository users;
    public PollController(PollRepository p,PollVoteRepository v,UserRepository u){polls=p;votes=v;users=u;}
    private User me(HttpSession s){Object id=s.getAttribute("userId");if(id==null)throw new IllegalArgumentException("Log in first");return users.findById(((Number)id).longValue()).orElseThrow();}
    private Map<String,Object> dto(Poll p,Long uid){Map<String,Object> m=new LinkedHashMap<>();m.put("id",p.getId());m.put("q",p.getQuestion());m.put("options",List.of(p.getOptionOne(),p.getOptionTwo()));m.put("v",List.of(p.getVotesOne(),p.getVotesTwo()));m.put("createdAt",p.getCreatedAt());m.put("voted",uid!=null&&votes.findByPollIdAndUserId(p.getId(),uid).isPresent());return m;}
    @GetMapping public List<Map<String,Object>> list(HttpSession s){Long uid=s.getAttribute("userId")==null?null:((Number)s.getAttribute("userId")).longValue();return polls.findAll().stream().sorted(Comparator.comparing(Poll::getCreatedAt).reversed()).map(p->dto(p,uid)).toList();}
    @PostMapping public Map<String,Object> create(@RequestBody Map<String,String> b,HttpSession s){User u=me(s);String q=Optional.ofNullable(b.get("q")).orElse("").trim(),a1=Optional.ofNullable(b.get("a1")).orElse("").trim(),a2=Optional.ofNullable(b.get("a2")).orElse("").trim();if(q.isBlank()||a1.isBlank()||a2.isBlank())throw new IllegalArgumentException("Add a question and two options");Poll p=new Poll();p.setQuestion(q);p.setOptionOne(a1);p.setOptionTwo(a2);p.setCreatedBy(u);return dto(polls.save(p),u.getId());}
    @PostMapping("/{id}/vote") public Map<String,Object> vote(@PathVariable Long id,@RequestBody Map<String,Object> b,HttpSession s){User u=me(s);Poll p=polls.findById(id).orElseThrow(()->new IllegalArgumentException("Poll not found"));int option=((Number)b.getOrDefault("option",-1)).intValue();if(option<0||option>1)throw new IllegalArgumentException("Invalid poll option");if(votes.findByPollIdAndUserId(id,u.getId()).isPresent())throw new IllegalArgumentException("You have already voted in this poll");PollVote v=new PollVote();v.setPoll(p);v.setUser(u);v.setOptionIndex(option);try{votes.saveAndFlush(v);}catch(DataIntegrityViolationException ex){throw new IllegalArgumentException("You have already voted in this poll");}if(option==0)p.setVotesOne(p.getVotesOne()+1);else p.setVotesTwo(p.getVotesTwo()+1);polls.save(p);return dto(p,u.getId());}
}

package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.Event;
import com.campusconnect.campusconnect.entity.User;
import com.campusconnect.campusconnect.repository.EventRepository;
import com.campusconnect.campusconnect.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api/events")
public class EventController {
    private final EventRepository events; private final UserRepository users;
    public EventController(EventRepository e,UserRepository u){events=e;users=u;}
    private User me(HttpSession s){Object id=s.getAttribute("userId");if(id==null)throw new IllegalArgumentException("Log in first");return users.findById(((Number)id).longValue()).orElseThrow();}
    private Map<String,Object> dto(Event e){Map<String,Object> m=new LinkedHashMap<>();m.put("id",e.getId());m.put("name",e.getName());m.put("date",e.getEventDate());m.put("place",e.getLocation());m.put("desc",e.getDescription());m.put("createdAt",e.getCreatedAt());m.put("createdBy",e.getCreatedBy().getFullName());return m;}
    @GetMapping public List<Map<String,Object>> list(){return events.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDateTime.now()).stream().map(this::dto).toList();}
    @PostMapping public Map<String,Object> create(@RequestBody Map<String,String> b,HttpSession s){User u=me(s);String name=Optional.ofNullable(b.get("name")).orElse("").trim(),place=Optional.ofNullable(b.get("place")).orElse("").trim();String date=Optional.ofNullable(b.get("date")).orElse("").trim();if(name.isBlank()||place.isBlank()||date.isBlank())throw new IllegalArgumentException("Event name, date and location are required");Event e=new Event();e.setName(name);e.setLocation(place);e.setDescription(b.getOrDefault("desc","").trim());try{e.setEventDate(LocalDateTime.parse(date));}catch(Exception ex){throw new IllegalArgumentException("Invalid event date");}if(e.getEventDate().isBefore(LocalDateTime.now()))throw new IllegalArgumentException("Event date must be in the future");e.setCreatedBy(u);return dto(events.save(e));}
}

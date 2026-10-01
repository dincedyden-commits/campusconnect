package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.dto.RegisterRequest;
import com.campusconnect.campusconnect.dto.UserResponse;
import com.campusconnect.campusconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok(Map.of("available", userService.isUsernameAvailable(username)));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(Map.of("available", userService.isEmailAvailable(email)));
    }

    @GetMapping("/search")
    public List<Map<String,Object>> search(@RequestParam String q) {
        if(q == null || q.isBlank()) return List.of();
        java.util.LinkedHashMap<Long, UserResponse> unique = new java.util.LinkedHashMap<>();
        userService.search(q).forEach(u -> unique.put(u.getId(), u));
        // System identities remain discoverable whenever a search matches CampusConnect.
        userService.search("campusconnect").stream()
                .filter(u -> UserService.isSystemUsername(u.getUsername()))
                .forEach(u -> unique.putIfAbsent(u.getId(), u));
        return unique.values().stream().map(u -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", u.getId()); m.put("username", u.getUsername());
            m.put("fullName", u.getFullName()); m.put("university", u.getUniversity()==null ? "" : u.getUniversity());
            m.put("role", u.getRole()); return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/discoverable")
    public List<Map<String,Object>> discoverable() {
        return userService.search("campusconnect").stream()
                .filter(u -> UserService.isSystemUsername(u.getUsername()))
                .map(u -> {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId()); m.put("username", u.getUsername());
                    m.put("fullName", u.getFullName()); m.put("university", u.getUniversity()==null?"":u.getUniversity());
                    m.put("role", u.getRole());
                    return m;
                }).collect(Collectors.toList());
    }

    @GetMapping("/customer-service")
    public ResponseEntity<?> customerService() {
        List<Map<String,Object>> contacts=userService.search("campusconnect").stream()
                .filter(u -> u.getUsername().equalsIgnoreCase("campusconnect") || u.getUsername().equalsIgnoreCase("campusconnectadmin"))
                .map(u -> {Map<String,Object> m=new LinkedHashMap<>();m.put("userId",u.getId());m.put("username",u.getUsername());m.put("fullName",u.getFullName());m.put("role",u.getRole());return m;}).collect(Collectors.toList());
        Map<String,Object> out=new LinkedHashMap<>();out.put("available",!contacts.isEmpty());out.put("contacts",contacts);if(contacts.isEmpty())out.put("message","Official customer service is not configured yet.");return ResponseEntity.ok(out);
    }

}

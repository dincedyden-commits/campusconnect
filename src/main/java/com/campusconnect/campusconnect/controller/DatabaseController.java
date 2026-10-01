package com.campusconnect.campusconnect.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class DatabaseController {
    private final JdbcTemplate jdbc;
    public DatabaseController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
    public Map<String,Object> health() {
        Map<String,Object> out = new LinkedHashMap<>();
        try {
            String db = jdbc.queryForObject("SELECT DATABASE()", String.class);
            Integer users = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer posts = jdbc.queryForObject("SELECT COUNT(*) FROM posts", Integer.class);
            out.put("connected", true);
            out.put("database", db);
            out.put("users", users);
            out.put("posts", posts);
            return out;
        } catch (Exception e) {
            out.put("connected", false);
            out.put("message", e.getMessage());
            return out;
        }
    }
}

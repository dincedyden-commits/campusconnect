package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {


private final GeminiService geminiService;

public AIController(GeminiService geminiService) {
    this.geminiService = geminiService;
}

@PostMapping("/ask")
public Map<String, String> ask(@RequestBody Map<String, String> body) throws Exception {
    String prompt = body.getOrDefault("prompt", "").trim();

    if (prompt.isEmpty()) {
        throw new IllegalArgumentException("Prompt cannot be empty");
    }

    String response = geminiService.ask(prompt);

    return Map.of("response", response);
}


}

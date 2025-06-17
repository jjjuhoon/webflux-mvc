package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final ChatService chatService;

    @PostMapping("/performance")
    public ResponseEntity<Map<String, Object>> testPerformance(@RequestBody List<String> questions) {
        long startTime = System.currentTimeMillis();

        List<String> responses = questions.stream()
                .map(question -> chatService.chatAnswer("test@gmail.com", "test_room", question))
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("responses", responses);
        result.put("totalTimeMs", endTime - startTime);

        return ResponseEntity.ok(result);
    }
}


package com.example.helloworldmvc.web.controller;

import com.example.helloworldmvc.service.PerformanceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.helloworldmvc.service.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final PerformanceTestService performanceTestService;
    private final ChatService chatService;

    @PostMapping("/ask")
    public String askQuestion(@RequestParam String gmail, @RequestParam String roomId, @RequestBody String question) {
        return chatService.chatAnswer(gmail, roomId, question);
    }
    @GetMapping("/performance/test")
    public String runPerformanceTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runSequentialTest(questionCount);
    }

    @GetMapping("/performance/parallel")
    public String runParallelPerformanceTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runParallelTest(questionCount);
    }

    @GetMapping("/mock/sequential")
    public String runMockSequentialTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runMockSequentialTest(questionCount);
    }

    @GetMapping("/mock/parallel")
    public String runMockParallelTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runMockParallelTest(questionCount);
    }
}

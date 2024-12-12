package com.example.helloworldmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PerformanceTestServiceImpl implements PerformanceTestService {

    private final ChatService chatService;
    private final MockChatService mockChatService;

    @Override
    public String runSequentialTest(int questionCount) {
        // Generate questions
        List<String> questions = generateQuestions(questionCount);

        List<Long> responseTimes = new ArrayList<>(); // Store response times
        long startTime = System.currentTimeMillis(); // Total start time

        // Process each question sequentially
        for (String question : questions) {
            long questionStartTime = System.currentTimeMillis();

            // Synchronous chat answer simulation
            chatService.chatAnswer("test@test.com", "675af66c517327f0e6c2bfs0", question);

            long questionEndTime = System.currentTimeMillis();
            responseTimes.add(questionEndTime - questionStartTime); // Measure response time
        }

        long totalTime = System.currentTimeMillis() - startTime; // Total execution time
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        return String.format("총 시간: %d ms, 평균 시간: %.2f ms", totalTime, averageTime);
    }

    @Override
    public String runParallelTest(int questionCount) {
        List<String> questions = generateQuestions(questionCount);
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 병렬 처리를 위한 스레드 풀
        List<Future<Long>> responseTimes = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (String question : questions) {
            Future<Long> future = executorService.submit(() -> {
                long questionStartTime = System.currentTimeMillis();
                chatService.chatAnswer("test@test.com", "new_chat", question); // 실제 GPT API 호출
                return System.currentTimeMillis() - questionStartTime;
            });
            responseTimes.add(future);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS); // 최대 1시간 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "테스트 중단됨.";
        }

        long totalTime = System.currentTimeMillis() - startTime;

        double averageTime = responseTimes.stream()
                .mapToLong(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .average()
                .orElse(0.0);

        return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms, 질문 개수: %d", totalTime, averageTime, questionCount);
    }



    @Override
    public String runMockSequentialTest(int questionCount) {
        List<String> questions = generateQuestions(questionCount);
        long startTime = System.currentTimeMillis();
        List<Long> responseTimes = new ArrayList<>();

        for (String question : questions) {
            long questionStartTime = System.currentTimeMillis();
            mockChatService.chatAnswer("test@test.com", "new_chat", question);
            long questionEndTime = System.currentTimeMillis();
            responseTimes.add(questionEndTime - questionStartTime);
        }

        long totalTime = System.currentTimeMillis() - startTime;
        double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms", totalTime, averageTime);
    }

    @Override
    public String runMockParallelTest(int questionCount) {
        List<String> questions = generateQuestions(questionCount);
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 병렬 처리를 위한 스레드 풀
        List<Future<Long>> responseTimes = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (String question : questions) {
            Future<Long> future = executorService.submit(() -> {
                long questionStartTime = System.currentTimeMillis();
                mockChatService.chatAnswer("test@test.com", "new_chat", question); // Mock 딜레이와 임의 응답 처리
                return System.currentTimeMillis() - questionStartTime;
            });
            responseTimes.add(future);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS); // 최대 1시간 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "테스트 중단됨.";
        }

        long totalTime = System.currentTimeMillis() - startTime;

        double averageTime = responseTimes.stream()
                .mapToLong(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .average()
                .orElse(0.0);

        return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms, 질문 개수: %d", totalTime, averageTime, questionCount);
    }



    private List<String> generateQuestions(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> "질문 " + (i + 1) + ": 외국인 노동자를 위한 E9 비자 정보는?")
                .toList();
    }
}

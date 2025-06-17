package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.repository.ChatMessageRepository;
import Helloworld.helloworld_webflux.repository.TranslateLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PerformanceTestServiceImpl implements PerformanceTestService {

    private final ChatService chatService;
    private final MockChatService mockChatService;
    private final ChatMessageRepository chatMessageRepository;
    private final TranslateLogRepository translateLogRepository;

    @Override
    public Mono<String> runSequentialTest(int questionCount) {
        // 질문 생성
        List<String> questions = generateQuestions(questionCount);

        long startTime = System.currentTimeMillis(); // 총 실행 시간 측정
        List<Long> responseTimes = new ArrayList<>(); // 각 질문별 응답 시간 저장

        return Flux.fromIterable(questions)
                .concatMap(question -> {
                    long questionStartTime = System.currentTimeMillis();
                    // chatAnswer 호출 및 응답 처리
                    return chatService.chatAnswer("test@test.com", "675af66c517c69663f3ad597", question)

                            .doOnNext(responses -> {
                                long questionEndTime = System.currentTimeMillis();
                                responseTimes.add(questionEndTime - questionStartTime);
                            });
                })
                .then(Mono.fromCallable(() -> {
                    long totalTime = System.currentTimeMillis() - startTime; // 총 시간
                    double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                    return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms", totalTime, averageTime);
                }));
    }
    @Override
    public Mono<String> runParallelTest(int questionCount) {
        // Generate questions
        List<String> questions = generateQuestions(questionCount);

        long startTime = System.currentTimeMillis(); // Start time
        List<Long> responseTimes = new ArrayList<>(); // Store response times for each question

        return Flux.fromIterable(questions)
                .flatMap(question -> {
                    long questionStartTime = System.currentTimeMillis();
                    return chatService.chatAnswer("test@test.com", "new_chat", question)

                            .doOnNext(responses -> {
                                long questionEndTime = System.currentTimeMillis();
                                responseTimes.add(questionEndTime - questionStartTime);
                            });
                })
                .collectList() // Wait for all responses
                .map(responses -> {
                    long totalTime = System.currentTimeMillis() - startTime; // Total execution time
                    double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                    return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms, 질문 개수: %d", totalTime, averageTime, questionCount);
                });
    }

    @Override
    public Mono<String> runMockSequentialTest(int questionCount) {
        List<String> questions = generateQuestions(questionCount);
        long startTime = System.currentTimeMillis();
        List<Long> responseTimes = new ArrayList<>();

        return Flux.fromIterable(questions)
                .concatMap(question -> {
                    long questionStartTime = System.currentTimeMillis();
                    return mockChatService.chatAnswer("test@test.com", "new_chat", question) // MockChatService 호출
                            .doOnNext(response -> {
                                long questionEndTime = System.currentTimeMillis();
                                responseTimes.add(questionEndTime - questionStartTime);
                            });
                })
                .then(Mono.fromCallable(() -> {
                    long totalTime = System.currentTimeMillis() - startTime;
                    double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                    return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms", totalTime, averageTime);
                }));
    }
    @Override
    public Mono<String> runMockParallelTest(int questionCount) {
        List<String> questions = generateQuestions(questionCount);
        long startTime = System.currentTimeMillis();

        return Flux.fromIterable(questions)
                .flatMap(question -> {
                    long questionStartTime = System.currentTimeMillis();
                    return mockChatService.chatAnswer("test@test.com", "new_chat", question)
                            .map(response -> System.currentTimeMillis() - questionStartTime);
                })
                .collectList() // Collect response times
                .map(responseTimes -> {
                    long totalTime = System.currentTimeMillis() - startTime;
                    double averageTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                    return String.format("총 시간: %d ms, 평균 응답 시간: %.2f ms, 총 질문 개수: %d", totalTime, averageTime, questionCount);
                });
    }




    private List<String> generateQuestions(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> "질문 " + (i + 1) + ": 외국인 노동자를 위한 E9 비자 정보는?")
                .toList();
    }

}


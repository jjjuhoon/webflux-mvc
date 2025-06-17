package Helloworld.helloworld_webflux.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class RootController {
    @Value("${openai.api.key}")
    private String openaiApiKey;
    private final WebClient webClient = WebClient.create("https://api.openai.com");

    @GetMapping("/health")
    public String health() {
        return "I'm Healthy!!!";
    }

    @GetMapping("/apikeytest")
    public Mono<String> ask() {
        return webClient.post()
                .uri("/v1/chat/completions")  // OpenAI의 챗봇 API 엔드포인트
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue("{ " +
                        "\"model\": \"gpt-4o\"," +
                        "\"messages\": [" +
                        "{\"role\": \"user\", \"content\": \"Hello, how are you?\"}" +
                        "]" +
                        "}")
                .retrieve()
                .bodyToMono(String.class)  // 응답을 String 으로 변환
                .onErrorReturn("Failed to connect to OpenAI API");  // 에러 발생 시 반환할 기본 값
    }




}

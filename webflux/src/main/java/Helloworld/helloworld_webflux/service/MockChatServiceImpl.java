package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.domain.ChatMessage;
import Helloworld.helloworld_webflux.domain.Room;
import Helloworld.helloworld_webflux.domain.TranslateLog;
import Helloworld.helloworld_webflux.repository.ChatMessageRepository;
import Helloworld.helloworld_webflux.repository.RoomRepository;
import Helloworld.helloworld_webflux.repository.TranslateLogRepository;
import Helloworld.helloworld_webflux.repository.UserRepository;
import Helloworld.helloworld_webflux.web.dto.ChatLogDTO;
import Helloworld.helloworld_webflux.web.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MockChatServiceImpl implements MockChatService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TranslateLogRepository translateLogRepository;
    private final RoomRepository roomRepository;

    @Override
    public Flux<String> chatAnswer(String gmail, String roomId, String question) {
        return findLanguage(gmail)
                .flatMapMany(language -> translateToKorean(question)
                        .flatMapMany(koreanQuestion -> createOrUpdateRoom(gmail, roomId, question)
                                .flatMapMany(room -> processMessages(room.getId(), question, koreanQuestion, language))));
    }

    private Mono<String> findLanguage(String gmail) {
        // Mock Language Detection (always return "English")
        return Mono.just("English");
    }

    @Override
    public Mono<String> translateToKorean(String text) {
        return Mono.just("Mock 번역된 질문: " + text)
                .delayElement(Duration.ofMillis(200)); // Mock 0.3초 딜레이
    }

    @Override
    public Mono<String> translateFromKorean(String text, String targetLanguage) {
        return Mono.just("Mock 번역된 응답: " + text)
                .delayElement(Duration.ofMillis(200)); // Mock 0.3초 딜레이
    }

    @Override
    public Mono<String> getChatbotResponse(JsonNode prompt) {
        return Mono.just("Mock GPT 응답")
                .delayElement(Duration.ofMillis(1000)); // Mock 1초 딜레이
    }

    private Flux<String> processMessages(String roomId, String question, String koreanQuestion, String language) {
        return getRecentTranslatedMessages(roomId)
                .collectList()
                .flatMapMany(recentMessages -> createPrompt(koreanQuestion, recentMessages)
                        .flatMapMany(prompt -> getChatbotResponse(prompt)
                                .flatMapMany(response -> processResponse(roomId, question, koreanQuestion, response, language))));
    }

    private Flux<String> processResponse(String roomId, String question, String koreanQuestion, String botResponse, String language) {
        return translateFromKorean(botResponse, language)
                .flatMapMany(userResponse ->
                        Flux.concat(
                                saveTranslatedMessage(roomId, "user", koreanQuestion),
                                saveTranslatedMessage(roomId, "bot", botResponse),
                                saveMessage(new ChatMessageDTO(null, roomId, "user", question, LocalDateTime.now())),
                                saveMessage(new ChatMessageDTO(null, roomId, "bot", userResponse, LocalDateTime.now()))
                        ).thenMany(Flux.just("Mock 응답: " + userResponse)));
    }

    @Override
    public Mono<ChatMessageDTO> saveMessage(ChatMessageDTO message) {
        ChatMessage entity = new ChatMessage(); // Mock Save (skip DB interaction)
        return Mono.just(message);
    }

    @Override
    public Flux<TranslateLog> getRecentTranslatedMessages(String roomId) {
        return Flux.empty(); // Mock Empty List
    }

    @Override
    public Mono<JsonNode> createPrompt(String koreanQuestion, List<TranslateLog> recentMessages) {
        ObjectNode root = new ObjectMapper().createObjectNode();
        ArrayNode conversationArray = root.putArray("Conversation");
        ObjectNode currentQuestionNode = conversationArray.addObject();
        currentQuestionNode.put("speaker", "human");
        currentQuestionNode.put("utterance", koreanQuestion);

        for (TranslateLog log : recentMessages) {
            ObjectNode messageNode = conversationArray.addObject();
            messageNode.put("speaker", log.getSender().equals("user") ? "human" : "system");
            messageNode.put("utterance", log.getContent());
        }

        return Mono.just(root);
    }

    @Override
    public Mono<Room> createOrUpdateRoom(String gmail, String roomId, String message) {
        return Mono.just(new Room()); // Mock Room Creation/Update
    }

    @Override
    public Mono<String> findRecentRoomAndLogs(String gmail) {
        // Mock response for findRecentRoomAndLogs
        return Mono.just("Mock Recent Room and Logs");
    }

    @Override
    public Mono<TranslateLog> saveTranslatedMessage(String roomId, String sender, String content) {
        TranslateLog log = new TranslateLog();
        log.setRoomId(roomId);
        log.setSender(sender);
        log.setContent(content);
        log.setTime(LocalDateTime.now());
        return Mono.just(log); // Mock saving
    }

    @Override
    public Flux<ChatMessage> getRecentMessages(String roomId) {
        return Flux.empty(); // Mock Empty Messages
    }
}


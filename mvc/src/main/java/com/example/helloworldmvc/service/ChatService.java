package com.example.helloworldmvc.service;

import com.example.helloworldmvc.converter.ChatMessageConverter;
import com.example.helloworldmvc.domain.ChatMessage;
import com.example.helloworldmvc.domain.Room;
import com.example.helloworldmvc.domain.TranslateLog;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.repository.ChatMessageRepository;
import com.example.helloworldmvc.repository.RoomRepository;
import com.example.helloworldmvc.repository.TranslateLogRepository;
import com.example.helloworldmvc.repository.UserRepository;
import com.example.helloworldmvc.web.dto.ChatMessageDTO;
import com.example.helloworldmvc.web.dto.GPTRequest;
import com.example.helloworldmvc.web.dto.GPTResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TranslateLogRepository translateLogRepository;
    private final RoomRepository roomRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openaiApiKey;
    @Transactional
    public String chatAnswer(String gmail, String roomId, String question) {
        String userLanguage = findUserLanguage(gmail).orElseThrow(() -> new IllegalArgumentException("No language found"));
        String koreanQuestion = translateToKorean(question);

        Room room = createOrUpdateRoom(gmail, roomId, question);
        List<TranslateLog> recentMessages = getRecentTranslatedMessages(room.getId());

        JsonNode prompt = createPrompt(koreanQuestion, recentMessages);
        String botResponse = getChatbotResponse(prompt);

        return processResponse(room.getId(), question, koreanQuestion, botResponse, userLanguage);
    }

    @Transactional
    public Optional<String> findUserLanguage(String gmail) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(gmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 미리 컬렉션 초기화
        Hibernate.initialize(user.getUserLanguageList()); // Lazy 컬렉션 초기화

        // 사용자의 첫 번째 언어 이름 반환
        return user.getUserLanguageList().stream()
                .map(userLanguage -> userLanguage.getLanguage().getName())
                .findFirst();
    }




    public String translateToKorean(String text) {
        GPTRequest request = new GPTRequest("gpt-3.5-turbo",
                List.of(
                        new GPTRequest.Message("system", "You are a translator."),
                        new GPTRequest.Message("user", "Translate this to Korean: " + text)
                ),
                1000);

        HttpEntity<GPTRequest> entity = new HttpEntity<>(request, createHeaders());
        GPTResponse response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions", entity, GPTResponse.class);

        return response != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "";
    }

    public String translateFromKorean(String text, String targetLanguage) {
        GPTRequest request = new GPTRequest("gpt-3.5-turbo",
                List.of(
                        new GPTRequest.Message("system", "You are a translator."),
                        new GPTRequest.Message("user", "Translate this to " + targetLanguage + ": " + text)
                ),
                1000);

        HttpEntity<GPTRequest> entity = new HttpEntity<>(request, createHeaders());
        GPTResponse response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions", entity, GPTResponse.class);

        return response != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "";
    }

    private String getChatbotResponse(JsonNode prompt) {
        GPTRequest request = new GPTRequest("gpt-3.5-turbo",
                List.of(
                        new GPTRequest.Message("system", "You are a helpful assistant."),
                        new GPTRequest.Message("user", prompt.get("Conversation").get(0).get("utterance").asText())
                ),
                1000);

        HttpEntity<GPTRequest> entity = new HttpEntity<>(request, createHeaders());
        GPTResponse response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions", entity, GPTResponse.class);

        return response != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "Error: Empty Response";
    }

    private String processResponse(String roomId, String question, String koreanQuestion, String botResponse, String language) {
        String userResponse = translateFromKorean(botResponse, language);

        saveTranslatedMessage(roomId, "user", koreanQuestion);
        saveTranslatedMessage(roomId, "bot", botResponse);

        saveMessage(new ChatMessageDTO(null, roomId, "user", question, LocalDateTime.now()));
        saveMessage(new ChatMessageDTO(null, roomId, "bot", userResponse, LocalDateTime.now()));

        return userResponse;
    }

    public void saveMessage(ChatMessageDTO message) {
        ChatMessage entity = ChatMessageConverter.toEntity(message);
        chatMessageRepository.save(entity);
    }

    public void saveTranslatedMessage(String roomId, String sender, String content) {
        TranslateLog log = new TranslateLog();
        log.setRoomId(roomId);
        log.setSender(sender);
        log.setContent(content);
        log.setTime(LocalDateTime.now());
        translateLogRepository.save(log);
    }

    private Room createOrUpdateRoom(String gmail, String roomId, String message) {
        if ("new_chat".equals(roomId)) {
            String title = message.length() > 20 ? message.substring(0, 17) + "..." : message;
            Room room = new Room();
            room.setTitle(title);
            room.setUpdatedAt(LocalDateTime.now());
            room.setUserId(userRepository.findByEmail(gmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId());
            return roomRepository.save(room);
        } else {
            return roomRepository.findById(roomId)
                    .map(existingRoom -> {
                        existingRoom.setUpdatedAt(LocalDateTime.now());
                        return roomRepository.save(existingRoom);
                    })
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        }
    }

    private List<TranslateLog> getRecentTranslatedMessages(String roomId) {
        return translateLogRepository.findTop10ByRoomIdOrderByTimeDesc(roomId);
    }

    private JsonNode createPrompt(String koreanQuestion, List<TranslateLog> recentMessages) {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode conversationArray = objectMapper.createArrayNode();

        ObjectNode currentQuestionNode = objectMapper.createObjectNode();
        currentQuestionNode.put("speaker", "human");
        currentQuestionNode.put("utterance", koreanQuestion);
        conversationArray.add(currentQuestionNode);

        for (TranslateLog message : recentMessages) {
            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("speaker", message.getSender().equals("user") ? "human" : "system");
            messageNode.put("utterance", message.getContent());
            conversationArray.add(messageNode);
        }

        root.set("Conversation", conversationArray);
        return root;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);
        return headers;
    }
}

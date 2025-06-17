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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public String chatAnswer(String gmail, String roomId, String question) {
        String userLanguage = findUserLanguage(gmail)
                .orElseThrow(() -> new IllegalArgumentException("No language found"));

        String koreanQuestion = translateToKorean(question);
        Room room = createOrUpdateRoom(gmail, roomId, question);
        List<TranslateLog> recentMessages = getRecentTranslatedMessages(room.getId());

        JsonNode prompt = createPrompt(koreanQuestion, recentMessages);
        String botResponse = getChatbotResponse(prompt);

        return processResponse(room.getId(), question, koreanQuestion, botResponse, userLanguage);
    }

    @Transactional
    public Optional<String> findUserLanguage(String gmail) {
        User user = userRepository.findByEmail(gmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Hibernate.initialize(user.getUserLanguageList());

        return user.getUserLanguageList().stream()
                .map(userLanguage -> userLanguage.getLanguage().getName())
                .findFirst();
    }

    // 실제 GPT 호출 대신 딜레이만 주고 원문 그대로 반환
    public String translateToKorean(String text) {
        simulateDelay();
        return text;
    }

    public String translateFromKorean(String text, String targetLanguage) {
        simulateDelay();
        return text;
    }

    private String getChatbotResponse(JsonNode prompt) {
        simulateDelay();
        return prompt.get("Conversation").get(0).get("utterance").asText(); // 입력값 그대로 반환
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

    private void simulateDelay() {
        try {
            Thread.sleep(1000); // 1초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

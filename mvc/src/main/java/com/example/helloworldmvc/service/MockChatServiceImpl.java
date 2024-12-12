package com.example.helloworldmvc.service;

import com.example.helloworldmvc.domain.Room;
import com.example.helloworldmvc.domain.TranslateLog;
import com.example.helloworldmvc.web.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MockChatServiceImpl implements MockChatService {

    @Override
    public String chatAnswer(String gmail, String roomId, String question) {
        String language = findLanguage(gmail);
        String koreanQuestion = translateToKorean(question);
        JsonNode prompt = createPrompt(koreanQuestion, getRecentTranslatedMessages(roomId));
        String botResponse = getChatbotResponse(prompt);
        return processResponse(roomId, question, koreanQuestion, botResponse, language);
    }

    private String findLanguage(String gmail) {
        // Mock Language Detection
        return "English";
    }

    @Override
    public String translateToKorean(String text) {
        delay(200); // Mock 0.3초 딜레이
        return "Mock 번역된 질문: " + text;
    }

    @Override
    public String translateFromKorean(String text, String targetLanguage) {
        delay(200); // Mock 0.3초 딜레이
        return "Mock 번역된 응답: " + text;
    }

    @Override
    public String getChatbotResponse(JsonNode prompt) {
        delay(1000); // Mock 1초 딜레이
        return "Mock GPT 응답";
    }

    @Override
    public String saveMessage(ChatMessageDTO message) {
        // Mock Save
        return "Message saved: " + message.getContent();
    }

    @Override
    public String saveTranslatedMessage(String roomId, String sender, String content) {
        // Mock Save Translated Message
        return String.format("Translated message saved: [%s] %s", sender, content);
    }

    @Override
    public List<TranslateLog> getRecentTranslatedMessages(String roomId) {
        // Mock: Return an empty list
        return new ArrayList<>();
    }

    @Override
    public JsonNode createPrompt(String koreanQuestion, List<TranslateLog> recentMessages) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode conversationArray = root.putArray("Conversation");

        // Add current question
        ObjectNode questionNode = conversationArray.addObject();
        questionNode.put("speaker", "human");
        questionNode.put("utterance", koreanQuestion);

        // Add recent messages
        for (TranslateLog log : recentMessages) {
            ObjectNode messageNode = conversationArray.addObject();
            messageNode.put("speaker", log.getSender().equals("user") ? "human" : "system");
            messageNode.put("utterance", log.getContent());
        }

        return root;
    }

    @Override
    public Room createOrUpdateRoom(String userId, String roomId, String message) {
        // Mock Room Creation or Update
        Room room = new Room();
        room.setId(roomId);
        room.setTitle("Mock Room: " + message);
        return room;
    }

    private String processResponse(String roomId, String question, String koreanQuestion, String botResponse, String language) {
        String userResponse = translateFromKorean(botResponse, language);
        saveTranslatedMessage(roomId, "user", koreanQuestion);
        saveTranslatedMessage(roomId, "bot", botResponse);
        saveMessage(new ChatMessageDTO(null, roomId, "user", question, null));
        saveMessage(new ChatMessageDTO(null, roomId, "bot", userResponse, null));
        return userResponse;
    }

    private void delay(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

package com.example.helloworldmvc.service;

import com.example.helloworldmvc.domain.Room;
import com.example.helloworldmvc.domain.TranslateLog;
import com.example.helloworldmvc.web.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface MockChatService {
    String chatAnswer(String gmail, String roomId, String question);
    String translateToKorean(String text);
    String translateFromKorean(String text, String targetLanguage);
    String getChatbotResponse(JsonNode prompt);
    String saveMessage(ChatMessageDTO message);
    String saveTranslatedMessage(String roomId, String sender, String content);
    List<TranslateLog> getRecentTranslatedMessages(String roomId);
    JsonNode createPrompt(String koreanQuestion, List<TranslateLog> recentMessages);
    Room createOrUpdateRoom(String userId, String roomId, String message);
}

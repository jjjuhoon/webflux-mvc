package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.domain.ChatMessage;
import Helloworld.helloworld_webflux.domain.Room;
import Helloworld.helloworld_webflux.domain.TranslateLog;
import Helloworld.helloworld_webflux.web.dto.ChatLogDTO;
import Helloworld.helloworld_webflux.web.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

public interface MockChatService {
    Flux<String> chatAnswer(String gmail, String roomId, String question);
    Mono<ChatMessageDTO> saveMessage(ChatMessageDTO message);
    Flux<ChatMessage> getRecentMessages(String roomId);
    Mono<String> translateToKorean(String text);
    Mono<String> translateFromKorean(String text, String targetLanguage);
    Mono<String> getChatbotResponse(JsonNode prompt);
    Mono<TranslateLog> saveTranslatedMessage(String roomId, String sender, String content);
    Flux<TranslateLog> getRecentTranslatedMessages(String roomId);
    Mono<JsonNode> createPrompt(String koreanQuestion, List<TranslateLog> recentMessages);
    Mono<Room> createOrUpdateRoom(String userId, String roomId, String message);
    Mono<String> findRecentRoomAndLogs(String gmail);
}

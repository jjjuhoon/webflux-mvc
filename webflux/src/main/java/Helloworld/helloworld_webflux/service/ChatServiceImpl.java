package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.converter.ChatMessageConverter;
import Helloworld.helloworld_webflux.domain.ChatMessage;
import Helloworld.helloworld_webflux.domain.Room;
import Helloworld.helloworld_webflux.domain.TranslateLog;
import Helloworld.helloworld_webflux.repository.ChatMessageRepository;
import Helloworld.helloworld_webflux.repository.RoomRepository;
import Helloworld.helloworld_webflux.repository.TranslateLogRepository;
import Helloworld.helloworld_webflux.repository.UserRepository;
import Helloworld.helloworld_webflux.web.dto.ChatLogDTO;
import Helloworld.helloworld_webflux.web.dto.ChatMessageDTO;
import Helloworld.helloworld_webflux.web.dto.GPTRequest;
import Helloworld.helloworld_webflux.web.dto.GPTResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;
    private final TranslateLogRepository translateLogRepository;
    private final RoomRepository roomRepository;

    private final WebClient webClient;
    private final UserRepository userRepository;

    @Override
    public Mono<String> chatAnswer(String gmail, String roomId, String question) {
        return userService.findLanguage(gmail)
                .flatMap(language -> translateToKorean(question)
                        .flatMap(koreanQuestion -> createOrUpdateRoom(gmail, roomId, question)
                                .flatMap(room -> getRoomAndProcessMessages(room.getId(), question, koreanQuestion, language))
                        )
                );
    }


    private Mono<String> getRoomAndProcessMessages(String updatedRoomId, String question, String koreanQuestion, String language) {
        return getRecentTranslatedMessages(updatedRoomId)
                .collectList()
                .flatMap(recentMessages -> createPrompt(koreanQuestion, recentMessages)
                        .flatMap(prompt -> getChatbotResponse(prompt)
                                .flatMap(response -> processResponse(updatedRoomId, question, koreanQuestion, response, language))
                        )
                );
    }


    private Mono<String> processResponse(String roomId, String question, String koreanQuestion, String botResponse, String language) {
        return translateFromKorean(botResponse, language)
                .flatMap(userResponse ->
                        Mono.when(
                                saveTranslatedMessage(roomId, "user", koreanQuestion),
                                saveTranslatedMessage(roomId, "bot", botResponse),
                                saveMessage(new ChatMessageDTO(null, roomId, "user", question, LocalDateTime.now())),
                                saveMessage(new ChatMessageDTO(null, roomId, "bot", userResponse, LocalDateTime.now()))
                        ).thenReturn(userResponse)
                );
    }






    @Override
    public Mono<ChatMessageDTO> saveMessage(ChatMessageDTO message) {
        ChatMessage entity = ChatMessageConverter.toEntity(message);
        return chatMessageRepository.save(entity).map(ChatMessageConverter::toDTO);
    }

    @Override
    public Flux<ChatMessage> getRecentMessages(String roomId) {
        return chatMessageRepository.findTop10ByRoomIdOrderByTimeDesc(roomId);
    }

    @Override
    public Mono<String> translateToKorean(String text) {
        return Mono.delay(Duration.ofSeconds(1))
                .thenReturn(text);
    }

    @Override
    public Mono<String> translateFromKorean(String text, String targetLanguage) {
        return Mono.delay(Duration.ofSeconds(1))
                .thenReturn(text);
    }

    @Override
    public Mono<String> getChatbotResponse(JsonNode prompt) {
        String originalText = prompt.get("Conversation").get(0).get("utterance").asText();
        return Mono.delay(Duration.ofSeconds(1))
                .thenReturn(originalText);
    }

    @Override
    public Mono<TranslateLog> saveTranslatedMessage(String roomId, String sender, String content) {
        TranslateLog log = new TranslateLog();
        log.setRoomId(roomId);
        log.setSender(sender);
        log.setContent(content);
        log.setTime(LocalDateTime.now());
        return translateLogRepository.save(log);
    }

    @Override
    public Flux<TranslateLog> getRecentTranslatedMessages(String roomId) {
        return translateLogRepository.findTop10ByRoomIdOrderByTimeDesc(roomId);
    }

    @Override
    public Mono<JsonNode> createPrompt(String koreanQuestion, List<TranslateLog> recentMessages) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode conversationArray = mapper.createArrayNode();

        // 질문 및 대화 메시지를 효율적으로 추가
        conversationArray.addObject()
                .put("speaker", "human")
                .put("utterance", koreanQuestion);

        recentMessages.forEach(message -> conversationArray.addObject()
                .put("speaker", message.getSender().equalsIgnoreCase("user") ? "human" : "system")
                .put("utterance", message.getContent()));

        ObjectNode root = mapper.createObjectNode();
        root.set("Conversation", conversationArray);

        return Mono.just(root);
    }



    @Override
    public Mono<Room> createOrUpdateRoom(String gmail, String roomId, String message) {
        // 만약 roomId가 "new_chat"인 경우 새 방 생성
        if ("new_chat".equals(roomId)) {
            String title = message.length() > 20 ? message.substring(0, 17) + "..." : message;
            Room room = new Room();
            return userRepository.findByEmail(gmail)
                    .flatMap(user -> {
                        room.setUserId(user.getId());
                        room.setTitle(title);
                        room.setUpdatedAt(LocalDateTime.now());
                        return roomRepository.save(room);  // Room 엔티티 저장
                    });
        } else {
            // 기존 방 업데이트
            return userRepository.findByEmail(gmail).flatMap(user -> {
                        return roomRepository.findByUserIdAndId(user.getId(), roomId)
                                .flatMap(existingRoom -> {
                                    existingRoom.setUpdatedAt(LocalDateTime.now());
                                    return roomRepository.save(existingRoom);
                                });
                    }
            );
        }
    }

    @Override
    public Mono<Tuple2<String, List<ChatLogDTO>>> findRecentRoomAndLogs(String gmail) {
        return userRepository.findByEmail(gmail).flatMap(user -> {
            return roomRepository.findFirstByUserIdOrderByUpdatedAtDesc(user.getId())
                    .flatMap(room -> chatMessageRepository.findByRoomIdOrderByTimeAsc(room.getId())
                            .collectList()
                            .map(messages -> Tuples.of(room.getId(), messages.stream()
                                    .map(this::toChatLogDTO)
                                    .collect(Collectors.toList()))
                            ));
        });
    }

    private ChatLogDTO toChatLogDTO(ChatMessage message) {
        return new ChatLogDTO(message.getContent(), message.getSender());
    }
}

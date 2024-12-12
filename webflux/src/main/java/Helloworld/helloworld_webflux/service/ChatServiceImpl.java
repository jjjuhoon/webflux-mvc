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

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Override
    public Flux<String> chatAnswer(String gmail, String roomId, String question) {
        return userService.findLanguage(gmail)
                .flatMapMany(language -> translateToKorean(question)
                        .flatMapMany(koreanQuestion -> createOrUpdateRoom(gmail, roomId, question)
                                .flatMapMany(room -> getRoomAndProcessMessages(room.getId(), question, koreanQuestion, language))
                        )
                );
    }

    private Flux<String> getRoomAndProcessMessages(String updatedRoomId, String question, String koreanQuestion, String language) {
        return getRecentTranslatedMessages(updatedRoomId)
                .collectList()
                .flatMapMany(recentMessages -> createPrompt(koreanQuestion, recentMessages)
                        .flatMapMany(prompt -> getChatbotResponse(prompt)
                                .flatMapMany(response -> processResponse(updatedRoomId, question, koreanQuestion, response, language))
                        )
                );
    }

    private Flux<String> processResponse(String roomId, String question, String koreanQuestion, String botResponse, String language) {
        return translateFromKorean(botResponse, language)
                .flatMapMany(userResponse ->
                        Flux.merge(
                                        saveTranslatedMessage(roomId, "user", koreanQuestion),
                                        saveTranslatedMessage(roomId, "bot", botResponse),
                                        saveMessage(new ChatMessageDTO(null, roomId, "user", question, LocalDateTime.now())),
                                        saveMessage(new ChatMessageDTO(null, roomId, "bot", userResponse, LocalDateTime.now()))
                                )
                                .then(Mono.just(userResponse + " | Room ID: " + roomId))
                                .flux()
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
        GPTRequest.Message systemMessage = new GPTRequest.Message("system", "You are a translator.");
        GPTRequest.Message userMessage = new GPTRequest.Message("user", "Exactly Translate the following text to Korean: " + text);
        GPTRequest request = new GPTRequest("gpt-3.5-turbo", List.of(systemMessage, userMessage), 1000);

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GPTResponse.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent());
    }

    @Override
    public Mono<String> translateFromKorean(String text, String targetLanguage) {
        GPTRequest.Message systemMessage = new GPTRequest.Message("system", "You are a translator.");
        GPTRequest.Message userMessage = new GPTRequest.Message("user", "you must use only" + targetLanguage + ". Exactly translate the following text to " + targetLanguage + ": " + text);
        GPTRequest request = new GPTRequest("gpt-3.5-turbo", List.of(systemMessage, userMessage), 1000);

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GPTResponse.class)
                .map(response -> response.getChoices().get(0).getMessage().getContent());
    }

    @Override
    public Mono<String> getChatbotResponse(JsonNode prompt) {
        // OpenAI의 GPT API와 통신
        GPTRequest.Message systemMessage = new GPTRequest.Message("system", "You are a helpful assistant.");
        GPTRequest.Message userMessage = new GPTRequest.Message("user", prompt.get("Conversation").get(0).get("utterance").asText());

        GPTRequest request = new GPTRequest("gpt-3.5-turbo", List.of(systemMessage, userMessage), 1000);

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions") // OpenAI API URL
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GPTResponse.class) // 응답을 GPTResponse 클래스로 매핑
                .map(response -> response.getChoices().get(0).getMessage().getContent()) // 첫 번째 응답 메시지 반환
                .onErrorResume(e -> {
                    System.err.println("Error occurred while calling OpenAI API: " + e.getMessage());
                    return Mono.just("An error occurred while processing your request.");
                });
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

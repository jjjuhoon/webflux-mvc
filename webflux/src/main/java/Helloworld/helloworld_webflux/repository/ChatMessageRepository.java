package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage,String> {
    Flux<ChatMessage> findTop10ByRoomIdOrderByTimeDesc(String roomId);
    Flux<ChatMessage> findByRoomIdOrderByTimeAsc(String roomId);

}

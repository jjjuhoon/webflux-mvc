package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.TranslateLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TranslateLogRepository extends ReactiveMongoRepository<TranslateLog, String> {
    Flux<TranslateLog> findTop10ByRoomIdOrderByTimeDesc(String roomId);
    Flux<TranslateLog> findByRoomIdOrderByTimeAsc(String roomId);

}

package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.Room;
import Helloworld.helloworld_webflux.domain.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoomRepository extends ReactiveMongoRepository<Room,String> {
    Mono<Room> findByUserIdAndId(Long userId,String roomId);
    Mono<Room> findFirstByUserIdOrderByUpdatedAtDesc(Long userId);
    Flux<Room> findByUserId(Long userId);

    @Query("SELECT u.* FROM users u INNER JOIN rooms r ON u.id = r.user_id WHERE r.id = :roomId")
    Mono<User> findUserByRoomId(String roomId);


}

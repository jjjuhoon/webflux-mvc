package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,Long>,UserCustomRepository {
    Mono<String> findLanguageByUserId(Long UserId);
    Mono<User> findByEmail(String email);

}

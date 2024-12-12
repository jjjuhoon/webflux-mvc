package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.User;
import reactor.core.publisher.Mono;

public interface UserCustomRepository {
    Mono<String> findLanguageByUserId(Long UserId);
    Mono<User> findByEmail(String email);

}

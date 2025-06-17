package Helloworld.helloworld_webflux.service;

import reactor.core.publisher.Mono;

public interface UserService {
    public Mono<String> findLanguage(String gmail);

}

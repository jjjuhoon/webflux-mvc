package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    public Mono<String> findLanguage(String gmail){
        return userRepository.findByEmail(gmail).flatMap(user -> userRepository.findLanguageByUserId(user.getId()));
    }
}

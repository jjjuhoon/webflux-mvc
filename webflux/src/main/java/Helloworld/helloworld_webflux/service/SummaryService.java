package Helloworld.helloworld_webflux.service;

import reactor.core.publisher.Mono;

public interface SummaryService {
    Mono<Void> generateSummary(String gmail, String roomId);


}

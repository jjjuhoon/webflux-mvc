package Helloworld.helloworld_webflux.service;

import reactor.core.publisher.Mono;

public interface PerformanceTestService {
    Mono<String> runSequentialTest(int questionCount);
    Mono<String> runParallelTest(int questionCount);
    Mono<String> runMockSequentialTest(int questionCount);

    Mono<String> runMockParallelTest(int questionCount);
}

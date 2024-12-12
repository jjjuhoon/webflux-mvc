package Helloworld.helloworld_webflux.web.controller;

import Helloworld.helloworld_webflux.config.auth.JwtTokenProvider;
import Helloworld.helloworld_webflux.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/summary")
public class SummaryController {
    private final SummaryService summaryService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Mono<Void> makeSummary(@RequestHeader("Authorization") String accessToken,
                                  @RequestParam("roomId") String roomId) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return summaryService.generateSummary(gmail, roomId);
    }
}

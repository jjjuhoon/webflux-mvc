package Helloworld.helloworld_webflux.web.controller;

import Helloworld.helloworld_webflux.config.auth.JwtTokenProvider;
import Helloworld.helloworld_webflux.domain.Room;
import Helloworld.helloworld_webflux.service.ChatService;
import Helloworld.helloworld_webflux.service.PerformanceTestService;
import Helloworld.helloworld_webflux.service.RoomService;
import Helloworld.helloworld_webflux.service.UserService;
import Helloworld.helloworld_webflux.web.dto.ChatLogDTO;
import Helloworld.helloworld_webflux.web.dto.RecentRoomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final RoomService roomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PerformanceTestService performanceTestService;

    @GetMapping("/language")
    public Mono<String> getLanguage(@RequestHeader("Authorization") String accessToken) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return userService.findLanguage(gmail);
    }

    @PostMapping("/ask")
    public Mono<String> askQuestion(@RequestParam String gmail,
                                    @RequestParam String roomId,
                                    @RequestBody String question) {
        return chatService.chatAnswer(gmail, roomId, question);
    }




    @GetMapping("/recent-room")
    public Mono<RecentRoomDTO> getRecentRoomAndLogs(@RequestHeader("Authorization") String accessToken) {
        String gmail = jwtTokenProvider.getGoogleEmail(accessToken);
        return chatService.findRecentRoomAndLogs(gmail)
                .map(roomAndLogs -> {
                    String roomId = roomAndLogs.getT1();
                    List<ChatLogDTO> logs = roomAndLogs.getT2();
                    return new RecentRoomDTO(roomId, logs);
                });
    }

    @GetMapping("/room-log")
    public Mono<RecentRoomDTO> getRoomAndLogs(@RequestParam("roomId") String roomId) {
        return roomService.findRoomLogs(roomId);
    }

    @GetMapping("/performance/sequential")
    public Mono<String> runPerformanceTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runSequentialTest(questionCount)
                .map(result -> "Total Time: " + result + "ms");
    }

    @GetMapping("/performance/parallel")
    public Mono<String> runParallelPerformanceTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runParallelTest(questionCount);
    }

    @GetMapping("/performance/mock/sequential")
    public Mono<String> runSequentialTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runMockSequentialTest(questionCount);
    }

    @GetMapping("/performance/mock/parallel")
    public Mono<String> runMockParallelTest(@RequestParam("count") int questionCount) {
        return performanceTestService.runMockParallelTest(questionCount);
    }

}

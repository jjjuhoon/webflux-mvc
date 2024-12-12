package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.web.dto.RecentRoomDTO;
import Helloworld.helloworld_webflux.web.dto.RoomDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoomService {
    Flux<RoomDTO> getUserRooms(String gmail);
    Mono<RecentRoomDTO> findRoomLogs(String roomId);
}
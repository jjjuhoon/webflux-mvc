package Helloworld.helloworld_webflux.service;

import Helloworld.helloworld_webflux.repository.ChatMessageRepository;
import Helloworld.helloworld_webflux.repository.RoomRepository;
import Helloworld.helloworld_webflux.repository.UserRepository;
import Helloworld.helloworld_webflux.web.dto.ChatLogDTO;
import Helloworld.helloworld_webflux.web.dto.RecentRoomDTO;
import Helloworld.helloworld_webflux.web.dto.RoomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Override
    public Flux<RoomDTO> getUserRooms(String gmail) {
        return userRepository.findByEmail(gmail)
                .flatMapMany(user -> roomRepository.findByUserId(user.getId())
                .map(room -> new RoomDTO(room.getId(), room.getTitle())));
    }

    @Override
    public Mono<RecentRoomDTO> findRoomLogs(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimeAsc(roomId)
                .map(chatMessage -> new ChatLogDTO(chatMessage.getContent(),chatMessage.getSender()))
                .collectList()
                .map(logs -> new RecentRoomDTO(roomId, logs));
    }
}

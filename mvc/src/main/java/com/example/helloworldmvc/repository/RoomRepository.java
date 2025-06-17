package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByUserId(String userId);
}
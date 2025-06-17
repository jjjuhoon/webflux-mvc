package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findTop10ByRoomIdOrderByTimeDesc(String roomId);
}
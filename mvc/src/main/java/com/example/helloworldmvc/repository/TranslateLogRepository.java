package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.TranslateLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TranslateLogRepository extends MongoRepository<TranslateLog, String> {
    List<TranslateLog> findTop10ByRoomIdOrderByTimeDesc(String roomId);
}

package Helloworld.helloworld_webflux.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat")
public class ChatMessage {
    @Id
    private String id;
    private String roomId;
    private String sender;
    private String content;
    private LocalDateTime time;
}
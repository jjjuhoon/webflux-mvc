package Helloworld.helloworld_webflux.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    private String title;
    private Long userId;
    private LocalDateTime updatedAt;
}

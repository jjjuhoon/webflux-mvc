package Helloworld.helloworld_webflux.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatLogDTO {
    private String content;
    private String sender;
}

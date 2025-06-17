package Helloworld.helloworld_webflux.converter;


import Helloworld.helloworld_webflux.domain.ChatMessage;
import Helloworld.helloworld_webflux.web.dto.ChatMessageDTO;

public class ChatMessageConverter {
    public static ChatMessageDTO toDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setRoomId(message.getRoomId());
        dto.setSender(message.getSender());
        dto.setContent(message.getContent());
        dto.setTime(message.getTime());
        return dto;
    }

    public static ChatMessage toEntity(ChatMessageDTO dto) {
        ChatMessage message = new ChatMessage();
        message.setId(dto.getId());
        message.setRoomId(dto.getRoomId());
        message.setSender(dto.getSender());
        message.setContent(dto.getContent());
        message.setTime(dto.getTime());
        return message;
    }
}

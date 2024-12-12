package Helloworld.helloworld_webflux.web.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentRoomDTO {
    private String roomId;
    private List<ChatLogDTO> chatLogs;
}

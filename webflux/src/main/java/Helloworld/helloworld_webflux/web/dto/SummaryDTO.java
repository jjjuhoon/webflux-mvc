package Helloworld.helloworld_webflux.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDTO {
    private String title;
    private String chatSummary;
    private String mainPoint;
}

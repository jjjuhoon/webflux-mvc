package com.example.helloworldmvc.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPTRequest {
    private String model;
    private List<Object> messages;
    private int max_tokens;

    public GPTRequest(List<Object> messages) {
        this.model = "gpt-3.5-turbo";  // 모델. gpt4가 더 좋으려나? - 의논
        this.messages = messages;
        this.max_tokens = 1000;  // 필요한 경우 토큰 수 조정
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageJson {
        //        private String role;
//        private String content;
        JsonNode contentJson;
    }
}

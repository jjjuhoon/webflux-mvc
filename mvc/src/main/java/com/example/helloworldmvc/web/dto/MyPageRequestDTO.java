package com.example.helloworldmvc.web.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class MyPageRequestDTO {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PatchProfile{
        String nickName;
        MultipartFile file;
    }
}

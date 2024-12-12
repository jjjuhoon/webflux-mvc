package com.example.helloworldmvc.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CommunityRequestDTO {

    @Getter
    @Setter
    public static class CreatePostDTO {
        @NotBlank
        String title;

        @NotBlank
        String content;

        List<MultipartFile> images;
    }
}

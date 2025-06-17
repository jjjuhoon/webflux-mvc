package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Community;
import com.example.helloworldmvc.domain.File;
import com.example.helloworldmvc.domain.User;

public class FileConverter {
    public static File toFile(String pictureUrl, User user, Center center) {
        return File.builder()
                .url(pictureUrl)
                .user(user)
                .center(center)
                .build();
    }
}

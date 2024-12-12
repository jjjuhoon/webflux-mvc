package com.example.helloworldmvc.web.dto;

import lombok.Getter;

import java.util.List;

public class CenterRequestDTO {

    @Getter
    public static class FilterLanguageReq{
        List<Long> languageList;
    }
}

package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.domain.Language;
import com.example.helloworldmvc.domain.mapping.UserLanguage;

import java.util.List;
import java.util.stream.Collectors;

public class UserLanguageConverter {
    public static List<UserLanguage> toUserLanguage(List<Language> languageList) {
        return languageList.stream()
                .map(language -> UserLanguage.builder()
                        .language(language)
                        .build()).collect(Collectors.toList());
    }
}

package com.example.helloworldmvc.service;

import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.web.dto.LanguageRequestDTO;

public interface LanguageService {
    String setUserLanguage(String userId, Long request);
}

package com.example.helloworldmvc.service;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.domain.Language;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.mapping.UserLanguage;
import com.example.helloworldmvc.repository.LanguageRepository;
import com.example.helloworldmvc.repository.UserLanguageRepository;
import com.example.helloworldmvc.repository.UserRepository;
import com.example.helloworldmvc.web.dto.LanguageRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LanguageServiceImpl implements LanguageService {

    private final UserLanguageRepository userLanguageRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    @Override
    public String setUserLanguage(String userId, Long languageId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.LANGUAGE_NOT_EXIST));

        UserLanguage userLanguage = userLanguageRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserLanguage newUserLanguage = UserLanguage.builder()
                            .user(user)
                            .language(language)
                            .build();
                    return userLanguageRepository.save(newUserLanguage);
                });

        if (!userLanguage.getLanguage().getId().equals(language.getId())) {
            userLanguage.setLanguage(language);
            userLanguageRepository.save(userLanguage);
        }

        return language.getName();
    }
}
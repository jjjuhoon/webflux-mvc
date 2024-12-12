package com.example.helloworldmvc.service;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.converter.UserLanguageConverter;
import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Counselor;
import com.example.helloworldmvc.domain.Language;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.mapping.UserLanguage;
import com.example.helloworldmvc.repository.*;
import com.example.helloworldmvc.web.dto.CenterRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CenterServiceImpl implements CenterService {

    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CounselorRepository counselorRepository;
    private final LanguageRepository languageRepository;


    @Override
    public Page<Center> getCenterListByDistance(double latitude, double longitude, Integer page, Integer size) {
        return centerRepository.findAllOrderByDistance(latitude, longitude, PageRequest.of(page, size));
    }


    @Override
    public Page<Counselor> getCounselorList(String userId, Long centerId, Integer page, Integer size) {
        User user = userRepository.findByEmail(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        centerRepository.findById(centerId).orElseThrow(() -> new GeneralException(ErrorStatus.CENTER_NOT_FOUND));
        List<Long> languageId = user.getUserLanguageList().stream()
                .map(language -> language.getLanguage().getId()).collect(Collectors.toList());
        return counselorRepository.findAllByCounselorLanguageList(languageId, centerId, PageRequest.of(page, size));

    }

    @Override
    public User createUserLanguage(String userId, Long centerId, CenterRequestDTO.FilterLanguageReq request) {
        User user = userRepository.findByEmail(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<Language> languageList = request.getLanguageList().stream()
                .map(language -> {
                    return languageRepository.findById(language).orElseThrow(() -> new GeneralException(ErrorStatus.LANGUAGE_NOT_FOUND));
                }).collect(Collectors.toList());
        centerRepository.findById(centerId).orElseThrow(() -> new GeneralException(ErrorStatus.CENTER_NOT_FOUND));
        List<UserLanguage> userLanguageList = UserLanguageConverter.toUserLanguage(languageList);
        userLanguageList.forEach(language -> language.setUser(user));
        return userRepository.save(user);
    }

    @Override
    public Center getCenter(String userId, Long centerId) {
        userRepository.findByEmail(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return centerRepository.findById(centerId).orElseThrow(() -> new GeneralException(ErrorStatus.CENTER_NOT_FOUND));
    }
}

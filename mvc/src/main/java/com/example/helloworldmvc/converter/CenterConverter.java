package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Counselor;
import com.example.helloworldmvc.domain.File;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.enums.CenterStatus;
import com.example.helloworldmvc.web.dto.CenterRequestDTO;
import com.example.helloworldmvc.web.dto.CenterResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class CenterConverter {
    public static CenterResponseDTO.CenterMapRes toCenterMapRes(Center center) {
        String centerImg = null;
        Optional<File> centerFileOptional = Optional.ofNullable(center.getFile());
        if (centerFileOptional.isPresent()) {
            centerImg = centerFileOptional.get().getUrl();
        }
        CenterStatus currentStatus=null;
        LocalTime now = LocalTime.now();
        if(now.isAfter(center.getOpened())&&now.isBefore(center.getClosed())){
            currentStatus=CenterStatus.OPEN;
        }
        else{
            currentStatus=CenterStatus.CLOSED;
        }
        return CenterResponseDTO.CenterMapRes.builder()
                .centerId(center.getId())
                .name(center.getName())
                .status(currentStatus)
                .closed(center.getClosed().toString())
                .address(center.getAddress())
                .image(centerImg)
                .latitude(center.getLatitude())
                .longitude(center.getLongitude())
                .build();
    }

    public static CenterResponseDTO.CenterMapListRes toCenterMapListRes(Page<Center> centerList,String userId) {
        List<CenterResponseDTO.CenterMapRes> centerMapRes = centerList.stream()
                .map(CenterConverter::toCenterMapRes).collect(Collectors.toList());
        return CenterResponseDTO.CenterMapListRes.builder()
                .centerMapList(centerMapRes)
                .userId(userId)
                .build();
    }

    public static CenterResponseDTO.CounselorListRes toCounselorListRes(Page<Counselor> counselorList,String userId) {
        List<CenterResponseDTO.CounselorRes> counselorResList = counselorList.stream()
                .map(CenterConverter::toCounselorRes).collect(Collectors.toList());
        return CenterResponseDTO.CounselorListRes.builder()
                .today(LocalDateTime.now())
                .counselorList(counselorResList)
                .userId(userId)
                .build();
    }
    public static CenterResponseDTO.CounselorRes toCounselorRes(Counselor counselor) {
        return CenterResponseDTO.CounselorRes.builder()
                .name(counselor.getName())
                .centerName(counselor.getCenter().getName())
                .language(counselor.getCounselorLanguageList().stream().map(s -> s.getLanguage().getName()).collect(Collectors.toList()))
                .start(counselor.getStart())
                .end(counselor.getEnd())
                .build();
    }

    public static CenterResponseDTO.FilterRes toFilterRes(User user) {
        return CenterResponseDTO.FilterRes.builder()
                .userId(user.getEmail())
                .build();
    }

    public static CenterResponseDTO.CenterDetailRes toCenterDetailRes(Center center,String userId) {
        return CenterResponseDTO.CenterDetailRes.builder()
                .detail(center.getDetails())
                .userId(userId)
                .build();
    }

}

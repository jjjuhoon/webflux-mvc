package com.example.helloworldmvc.web.dto;

import com.example.helloworldmvc.domain.Language;
import com.example.helloworldmvc.domain.enums.CenterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CenterResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CenterMapListRes {
        List<CenterResponseDTO.CenterMapRes> centerMapList;
        String userId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CenterMapRes {
        Long centerId;
        String name;
        CenterStatus status;
        String closed;
        String address;
        //MultipartFile 형으로 변경예정
        String image;
        Double latitude;
        Double longitude;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselorListRes {
        LocalDateTime today;
        List<CounselorRes> counselorList;
        String userId;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselorRes {
        String name;
        String centerName;
        List<String> language;
        LocalDateTime start;
        LocalDateTime end;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterRes {
        String userId;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CenterDetailRes {
        String detail;
        String userId;
    }
}

package com.example.helloworldmvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MyPageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageResDTO{
        Long userId;
        String name;
        String userImg;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllSummaryListRes {
        String userId;
        List<MyPageResponseDTO.AllSummaryRes> allsummaryList;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllSummaryRes{
        Long summaryId;
        String identificationNum;
        LocalDateTime uploadedAt;
        String name;
        String userImg;
        String title;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailSummaryRes{
        String userId;
        Long summaryId;
        String identificationNum;
        LocalDateTime uploadedAt;
        String name;
        String userImg;
        String chatSummary;
        String mainPoint;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllReservationListRes {
        Long userId;
        List<MyPageResponseDTO.AllReservationRes> allReservationList;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllReservationRes{
        Long summaryId;
        String identificationNum;
        LocalDateTime uploadedAt;
        String name;
        String userImg;
        String title;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchProfileEmail{
        String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCommunityListResDTO {
        Long userId;
        List<MyCommunityResDTO> allMyCommunityList;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCommunityResDTO {
        Long communityId;
        String title;
        LocalDateTime uploadedAt;
        String category;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCommentListResDTO {
        Long userId;
        List<MyCommentResDTO> allMyCommentList;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCommentResDTO {
        Long communityId;
        Long commentId;
        String commentContent;
        LocalDateTime uploadedAt;
        String communityTitle;
    }
}

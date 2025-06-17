package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.domain.*;
import com.example.helloworldmvc.domain.mapping.Reservation;
import com.example.helloworldmvc.web.dto.MyPageResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyPageConverter {

    public static MyPageResponseDTO.MyPageResDTO toMyPageRes(User user){
        String userImg = null;

        Optional<File> userFileOptional = Optional.ofNullable(user.getFile());
        if(userFileOptional.isPresent())userImg=user.getFile().getUrl();

        if (userFileOptional.isPresent()) {
            userImg = userFileOptional.get().getUrl();
        }
        return MyPageResponseDTO.MyPageResDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .userImg(userImg)
                .build();

    }

    public static MyPageResponseDTO.AllSummaryRes toAllSummaryRes(Summary summary) {
        User user = summary.getUser();
        String userImg = null;
        Optional<File> userFileOptional = Optional.ofNullable(user.getFile());
        if (userFileOptional.isPresent()) {
            userImg = userFileOptional.get().getUrl();
        }
        return MyPageResponseDTO.AllSummaryRes.builder()
                .summaryId(summary.getId())
                .identificationNum(summary.getIdentificationNum())
                .uploadedAt(summary.getUpdateAt())
                .name(user.getName())
                .userImg(userImg)
                .title(summary.getTitle())
                .build();
    }

    public static MyPageResponseDTO.AllSummaryListRes toAllSummaryListRes(Page<Summary> summaryList,String userId) {
        List<MyPageResponseDTO.AllSummaryRes> allSummaryRes = summaryList.stream()
                .map(MyPageConverter::toAllSummaryRes).collect(Collectors.toList());
        return MyPageResponseDTO.AllSummaryListRes.builder()
                .userId(userId)
                .allsummaryList(allSummaryRes)
                .build();
    }

    public static MyPageResponseDTO.DetailSummaryRes toDetailSummaryRes(Summary summary){
        User user=summary.getUser();
        String userImg = null;
        Optional<File> userFileOptional = Optional.ofNullable(user.getFile());
        if (userFileOptional.isPresent()) {
            userImg = userFileOptional.get().getUrl();
        }
        return MyPageResponseDTO.DetailSummaryRes.builder()
                .userId(user.getEmail())
                .summaryId(summary.getId())
                .identificationNum(summary.getIdentificationNum())
                .uploadedAt(summary.getUpdateAt())
                .name(user.getName())
                .userImg(userImg)
                .chatSummary(summary.getChatSummary())
                .mainPoint(summary.getMainPoint())
                .build();
    }

    public static MyPageResponseDTO.AllReservationRes toAllReservationRes(Reservation reservation) {
        User user=reservation.getUser();
        Summary summary=user.getUserSummaryList().stream()
                .max(Comparator.comparing(Summary::getCreatedAt)).orElseThrow(() -> new GeneralException(ErrorStatus.SUMMARY_NOT_FOUND));;
        String userImg = null;
        Optional<File> userFileOptional = Optional.ofNullable(user.getFile());
        if (userFileOptional.isPresent()) {
            userImg = userFileOptional.get().getUrl();
        }
        return MyPageResponseDTO.AllReservationRes.builder()
                .summaryId(summary.getId())
                .identificationNum(summary.getIdentificationNum())
                .uploadedAt(summary.getUpdateAt())
                .name(user.getName())
                .userImg(userImg)
                .title(summary.getTitle())
                .build();
    }

    public static MyPageResponseDTO.AllReservationListRes toAllReservationListRes(Page<Reservation> reservationList,Long userId) {

        List<MyPageResponseDTO.AllReservationRes> allReservationRes = reservationList.stream()
                .map(MyPageConverter::toAllReservationRes).collect(Collectors.toList());
        return MyPageResponseDTO.AllReservationListRes.builder()
                .userId(userId)
                .allReservationList(allReservationRes)
                .build();
    }



    public static MyPageResponseDTO.MyCommunityResDTO toMyCommunityRes(Community community) {
        return MyPageResponseDTO.MyCommunityResDTO.builder()
                .communityId(community.getId())
                .title(community.getTitle())
                .uploadedAt(community.getCreatedAt())
                .category(community.getCommunityCategory().name())
                .build();
    }

    public static MyPageResponseDTO.MyCommunityListResDTO toAllMyCommunityListRes(Page<MyPageResponseDTO.MyCommunityResDTO> communityList, Long userId) {
        return MyPageResponseDTO.MyCommunityListResDTO.builder()
                .userId(userId)
                .allMyCommunityList(communityList.getContent())
                .build();
    }

    public static MyPageResponseDTO.MyCommentResDTO toMyCommentRes(Comment comment) {
        return MyPageResponseDTO.MyCommentResDTO.builder()
                .communityId(comment.getCommunity().getId())
                .commentId(comment.getId())
                .commentContent(comment.getContent())
                .uploadedAt(comment.getCreatedAt())
                .communityTitle(comment.getCommunity().getTitle())
                .build();
    }

    public static MyPageResponseDTO.MyCommentListResDTO toMyCommentListRes(Page<MyPageResponseDTO.MyCommentResDTO> commentList, Long userId) {
        return MyPageResponseDTO.MyCommentListResDTO.builder()
                .userId(userId)
                .allMyCommentList(commentList.getContent())
                .build();
    }
}

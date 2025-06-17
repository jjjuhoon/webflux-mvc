package com.example.helloworldmvc.service;

import com.example.helloworldmvc.domain.Summary;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.mapping.Reservation;
import com.example.helloworldmvc.web.dto.MyPageRequestDTO;
import com.example.helloworldmvc.web.dto.MyPageResponseDTO;
import org.springframework.data.domain.Page;

public interface MyPageService {
    User getUser(String userId);
    Summary getSummary(Long summaryId);
    Page<Summary> getSummaryList(String userId, Integer page, Integer size);
    Page<Reservation> getReservationList(Long counselorId, Integer page, Integer size);

    void setUserProfile(String gmail, MyPageRequestDTO.PatchProfile request);

    String deactivateUser(String userId);

    MyPageResponseDTO.MyCommunityListResDTO getCommunityList(String userId, Integer page, Integer size);

    MyPageResponseDTO.MyCommentListResDTO getAllCommentsByUser(String userId, Integer page, Integer size);

}

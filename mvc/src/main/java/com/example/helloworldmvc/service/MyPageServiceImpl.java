package com.example.helloworldmvc.service;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.converter.MyPageConverter;
import com.example.helloworldmvc.domain.*;
import com.example.helloworldmvc.domain.mapping.Reservation;
import com.example.helloworldmvc.repository.*;
import com.example.helloworldmvc.web.dto.MyPageRequestDTO;
import com.example.helloworldmvc.web.dto.MyPageResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.helloworldmvc.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService{
    private final UserRepository userRepository;
    private final SummaryRepository summaryRepository;
    private final CounselorRepository counselorRepository;
    private final ReservationRepository reservationRepository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;



    @Override
    public User getUser(String userId) {
        return userRepository.findByEmail(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public Summary getSummary(Long summaryId) {
        return summaryRepository.findById(summaryId).orElseThrow(() -> new GeneralException(ErrorStatus.SUMMARY_NOT_FOUND));
    }

    @Override
    public Page<Summary> getSummaryList(String userId, Integer page, Integer size) {
        userRepository.findByEmail(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return summaryRepository.findAllByUserEmail(userId, PageRequest.of(page, size));
    }

    @Override
    public Page<Reservation> getReservationList(Long counselorId, Integer page, Integer size) {
        counselorRepository.findById(counselorId).orElseThrow(() -> new GeneralException(ErrorStatus.COUNSELOR_NOT_FOUND));
        return reservationRepository.findAllByCounselorId(counselorId, PageRequest.of(page, size));
    }

    @Override
    public void setUserProfile(String gmail, MyPageRequestDTO.PatchProfile request){
        User user=userRepository.findByEmail(gmail).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
//        Long userId = jwtTokenProvider.getCurrentUser(request);
//
//        Optional<User> optionalUser = userRepository.findById(userId);
//        if (optionalUser.isEmpty()) {
//            userService.checkUser(false);
//        }
//        User user = optionalUser.get();


        Optional<File> optionalFile=fileRepository.findByUserId(user.getId());
        File newFile = null;
        if(optionalFile.isPresent() && request.getFile() != null){
            newFile=s3Service.changeImage(request.getFile(), user);
            user.setFile(newFile);
        }else if(request.getFile() != null){
            newFile=s3Service.setImage(request.getFile(), user);
            user.setFile(newFile);
        }
        user.setName(request.getNickName());
        userRepository.save(user);
    }

    @Override
    public String deactivateUser(String userId) {
        User user = userRepository.findByEmail(userId).orElseThrow(() -> new GeneralException(USER_NOT_FOUND));
        user.setStatusTempDeactivated();
        userRepository.save(user);
        return  userId+" 유저가 삭제 되었습니다";
    }

    @Override
    public MyPageResponseDTO.MyCommunityListResDTO getCommunityList(String userId, Integer page, Integer size) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Page<Community> communityPage = communityRepository.findAllByUserId(user.getId(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<MyPageResponseDTO.MyCommunityResDTO> communityResPage = communityPage.map(MyPageConverter::toMyCommunityRes);

        return MyPageConverter.toAllMyCommunityListRes(communityResPage, user.getId());
    }

    @Override
    public MyPageResponseDTO.MyCommentListResDTO getAllCommentsByUser(String userId, Integer page, Integer size) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Page<Comment> commentPage = commentRepository.findAllByUserId(user.getId(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<MyPageResponseDTO.MyCommentResDTO> commentResPage = commentPage.map(MyPageConverter::toMyCommentRes);

        return MyPageConverter.toMyCommentListRes(commentResPage, user.getId());
    }
}

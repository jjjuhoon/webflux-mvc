package com.example.helloworldmvc.service;

import com.example.helloworldmvc.apiPayload.GeneralException;
import com.example.helloworldmvc.apiPayload.code.status.ErrorStatus;
import com.example.helloworldmvc.converter.CommentConverter;
import com.example.helloworldmvc.converter.CommunityConverter;
import com.example.helloworldmvc.domain.Comment;
import com.example.helloworldmvc.domain.Community;
import com.example.helloworldmvc.domain.File;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.repository.CommentRepository;
import com.example.helloworldmvc.repository.CommunityRepository;
import com.example.helloworldmvc.repository.UserRepository;
import com.example.helloworldmvc.web.dto.CommentRequestDTO;
import com.example.helloworldmvc.web.dto.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;


    @Override
    public CommentResponseDTO.commentCreateRes createComment(String userId, Long communityId, CommentRequestDTO.commentCreateReq requestBody) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_NOT_FOUND));

        List<Comment> existingComments = commentRepository.findByUserAndCommunity(user, community);
        Long anonymousNumber = existingComments.isEmpty() ?
                commentRepository.findMaxAnonymousInCommunity(community) + 1 :
                existingComments.get(0).getAnonymous();

        Comment comment = CommentConverter.toComment(requestBody, community, user, anonymousNumber);
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDTO.commentCreateRes(savedComment.getId());
    }
}

package com.example.helloworldmvc.service;

import com.example.helloworldmvc.domain.Comment;
import com.example.helloworldmvc.web.dto.CommentRequestDTO;
import com.example.helloworldmvc.web.dto.CommentResponseDTO;

public interface CommentService {
    public CommentResponseDTO.commentCreateRes createComment(String userId, Long communityId, CommentRequestDTO.commentCreateReq requestBody);

}

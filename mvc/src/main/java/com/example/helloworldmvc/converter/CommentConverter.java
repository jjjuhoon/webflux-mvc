package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.domain.Comment;
import com.example.helloworldmvc.domain.Community;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.web.dto.CommentRequestDTO;

public class CommentConverter {

    public static Comment toComment(CommentRequestDTO.commentCreateReq request, Community community, User user, Long anonymous) {
        return Comment.builder()
                .user(user)
                .community(community)
                .anonymous(anonymous)
                .content(request.getContent())
                .build();
    }
}

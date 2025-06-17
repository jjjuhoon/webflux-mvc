package com.example.helloworldmvc.service;

import com.example.helloworldmvc.domain.Community;
import com.example.helloworldmvc.web.dto.CommunityRequestDTO;
import com.example.helloworldmvc.web.dto.CommunityResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityService {

    CommunityResponseDTO.CreatedPostDTO createCommunityPost(String userId, Long categoryId, CommunityRequestDTO.CreatePostDTO request);
    CommunityResponseDTO.PostListDTO getCommunityList(String userId, Long categoryId, Integer page, Integer size);
    CommunityResponseDTO.PostDetailDTO getCommunityDetail(String userId, Long communityId, Integer page, Integer size);
}

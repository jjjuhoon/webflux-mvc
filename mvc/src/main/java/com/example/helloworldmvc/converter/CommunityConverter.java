package com.example.helloworldmvc.converter;

import com.example.helloworldmvc.domain.Comment;
import com.example.helloworldmvc.domain.Community;
import com.example.helloworldmvc.domain.enums.CommunityCategory;
import com.example.helloworldmvc.web.dto.CommunityRequestDTO;
import com.example.helloworldmvc.web.dto.CommunityResponseDTO;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityConverter {
    public static Community toCommunityPost(CommunityRequestDTO.CreatePostDTO createPostDTO, Long categoryId){
        CommunityCategory communityCategory = toCommunityCategory(categoryId);
        return Community.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .communityCategory(communityCategory)
                .fileList(new ArrayList<>())
                .build();
    }
    public static CommunityResponseDTO.CreatedPostDTO toCreatedPostDTO(Community community){
        return CommunityResponseDTO.CreatedPostDTO.builder()
                .community_id(community.getId())
                .build();
    }

    public static CommunityResponseDTO.PostListDTO toPostListDTO(Page<Community> postList){
        List<CommunityResponseDTO.PostDTO> posts = postList.stream().map(CommunityConverter::toPostDTO).toList();
        return CommunityResponseDTO.PostListDTO.builder()
                .postDTOList(posts)
                .build();
    }

    public static CommunityResponseDTO.PostDTO toPostDTO(Community community){
        String imageUrl = "null";
        if(!community.getFileList().isEmpty()){
            imageUrl = community.getFileList().get(0).getUrl();
        }
        return CommunityResponseDTO.PostDTO.builder()
                .post_id(community.getId())
                .title(community.getTitle())
                .created_at(community.getCreatedAt())
                .commentNum(community.getCommentList().size())
                .imageUrl(imageUrl)
                .build();
    }

    public static CommunityResponseDTO.PostDetailDTO toPostDetailDTO(Community community, Page<Comment> commentList){
        List<String> list = new ArrayList<>();
        if(!community.getFileList().isEmpty()){
            community.getFileList().stream().map(file -> list.add(file.getUrl())).collect(Collectors.toList());
        }
        else list.add("NULL");
        List<CommunityResponseDTO.CommentDTO> comments = commentList.stream().map(CommunityConverter::toCommentDTO).toList();
        return CommunityResponseDTO.PostDetailDTO.builder()
                .title(community.getTitle())
                .content(community.getContent())
                .created_at(community.getCreatedAt())
                .fileList(list)
                .commentDTOList(comments)
                .build();
    }

    public static CommunityResponseDTO.CommentDTO toCommentDTO(Comment comment){
        return CommunityResponseDTO.CommentDTO.builder()
                .anonymousName(comment.getAnonymous())
                .created_at(comment.getCreatedAt())
                .content(comment.getContent())
                .build();
    }
    public static CommunityCategory toCommunityCategory(Long categoryId){
        CommunityCategory communityCategory = null;
        switch (categoryId.intValue()){
            case 0:
                communityCategory = CommunityCategory.WORRY;
                break;
            case 1:
                communityCategory = CommunityCategory.MEDICAL;
                break;
            case 2:
                communityCategory = CommunityCategory.QUALIFICATION;
                break;
            default:
                communityCategory = CommunityCategory.ETC;
                break;
        }
        return communityCategory;
    }
}

package com.example.helloworldmvc.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    public void setUrl(String imageUrl){
        this.url = imageUrl;
    }

    public void setCommunity(Community community){
        if(this.community != null){
            community.getFileList().remove(this);
        }
        this.community = community;
        community.getFileList().add(this);
    }

}

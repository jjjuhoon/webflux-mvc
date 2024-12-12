package com.example.helloworldmvc.domain.mapping;

import com.example.helloworldmvc.domain.Language;
import com.example.helloworldmvc.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    public void setUser(User user) {
        if(this.user != null){
            user.getUserLanguageList().remove(this);
        }
        this.user = user;
        user.getUserLanguageList().add(this);
    }
    public void setLanguage(Language language){
        this.language = language;
    }
}

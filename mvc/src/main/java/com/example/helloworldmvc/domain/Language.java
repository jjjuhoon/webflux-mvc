package com.example.helloworldmvc.domain;

import com.example.helloworldmvc.domain.mapping.CounselorLanguage;
import com.example.helloworldmvc.domain.mapping.UserLanguage;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 10)
    private String name;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL)
    private List<UserLanguage> userLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL)
    private List<CounselorLanguage> counselorLanguages = new ArrayList<>();
}

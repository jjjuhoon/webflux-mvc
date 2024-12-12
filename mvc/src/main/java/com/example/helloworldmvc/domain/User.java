package com.example.helloworldmvc.domain;

import com.example.helloworldmvc.domain.common.BaseEntity;
import com.example.helloworldmvc.domain.enums.Role;
import com.example.helloworldmvc.domain.enums.UserStatus;
import com.example.helloworldmvc.domain.mapping.UserLanguage;
import com.example.helloworldmvc.domain.mapping.Reservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String email;

    @Column
    private LocalDateTime deactivationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING) // Enum 타입은 문자열 형태로 저장해야 함
    @NotNull
    private Role role;




    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Summary> userSummaryList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private File file;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reservation> reservationList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLanguage> userLanguageList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Community> communityList = new ArrayList<>();

    public User update(String name) {
        this.name = name;

        return this;
    }
    public String getRoleKey() {
        return this.role.getKey();
    }

    public void setName(String name){
        this.name = name;
    }
    public void setFile(File file) {
        this.file = file;
    }

    //특정 기간 이후 탈퇴 처리될 예정. 아직 사용x
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.deactivationDate = null;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
        this.deactivationDate = null;
    }

    public void setStatusTempDeactivated() {
        this.status = UserStatus.TEMP_DEACTIVATED;
        this.deactivationDate = LocalDateTime.now();
    }
}


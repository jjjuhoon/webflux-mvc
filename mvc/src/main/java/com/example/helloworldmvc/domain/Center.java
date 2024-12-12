package com.example.helloworldmvc.domain;

import com.example.helloworldmvc.domain.enums.CenterStatus;
import com.example.helloworldmvc.domain.mapping.Reservation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'CLOSED'")
    private CenterStatus status;

    @Column(nullable = true)
    private LocalTime closed;

    @Column(nullable = true)
    private LocalTime opened;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = true, length = 255)
    private String details;

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL)
    private List<Counselor> counselorList = new ArrayList<>();

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL)
    private List<Reservation> reservationList = new ArrayList<>();

    @OneToOne(mappedBy = "center", cascade = CascadeType.ALL)
    private File file;

}

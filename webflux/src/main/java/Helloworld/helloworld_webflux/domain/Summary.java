package Helloworld.helloworld_webflux.domain;

import Helloworld.helloworld_webflux.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("summary")
public class Summary {
    @Id
    private Long id;
    private String identificationNum;
    private String status;
    private String title;
    private String chatSummary;
    private String mainPoint;
    private Long userId;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("update_at")
    private LocalDateTime updatedAt;
}

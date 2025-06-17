package Helloworld.helloworld_webflux.domain;


import Helloworld.helloworld_webflux.domain.common.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table("uuids")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Uuid extends BaseEntity {
    @Id
    private Long id;

    @Column("uuid")
    private String uuid;
}

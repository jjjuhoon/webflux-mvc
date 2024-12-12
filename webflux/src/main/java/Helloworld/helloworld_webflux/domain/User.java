package Helloworld.helloworld_webflux.domain;

import Helloworld.helloworld_webflux.domain.common.BaseEntity;
import Helloworld.helloworld_webflux.domain.mapping.UserLanguage;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Table("users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @MappedCollection(idColumn = "user_id")
    private List<UserLanguage> userLanguageList = new ArrayList<>();
}
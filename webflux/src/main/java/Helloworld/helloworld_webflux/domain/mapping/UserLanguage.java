package Helloworld.helloworld_webflux.domain.mapping;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_languages")
public class UserLanguage {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("language_id")
    private Long languageId;
}
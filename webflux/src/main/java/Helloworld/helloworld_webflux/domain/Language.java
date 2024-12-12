package Helloworld.helloworld_webflux.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("languages")
public class Language {
    @Id
    private Long id;
    private String name;
}
package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{
    private final DatabaseClient databaseClient;
    @Override
    public Mono<String> findLanguageByUserId(Long userId) {
        var sql= """
                SELECT l.name
                FROM summary.language l
                INNER JOIN summary.user_language ul ON l.id = ul.language_id
                WHERE ul.user_id = :userId
                """;
        return databaseClient.sql(sql)
                .bind("userId", userId)
                .map(row -> row.get("name", String.class))
                .one();  // 결과가 하나인 것을 기대하며 Mono로 반환
    }
    @Override
    public Mono<User> findByEmail(String email) {
        return databaseClient.sql("SELECT u.id, u.name, u.email FROM summary.user u WHERE u.email = :email")
                .bind("email", email)  // 파라미터 바인딩
                .map(row -> User.builder()
                        .id((Long) row.get("id"))
                        .name((String) row.get("name"))
                        .email((String) row.get("email"))
                        .build()
                )
                .one();
    }
}

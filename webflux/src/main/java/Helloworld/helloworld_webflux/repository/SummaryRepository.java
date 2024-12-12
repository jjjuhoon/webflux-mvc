package Helloworld.helloworld_webflux.repository;

import Helloworld.helloworld_webflux.domain.Summary;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SummaryRepository extends R2dbcRepository<Summary, Long> {
}

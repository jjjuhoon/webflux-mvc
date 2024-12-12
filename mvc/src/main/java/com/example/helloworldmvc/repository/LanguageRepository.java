package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
}

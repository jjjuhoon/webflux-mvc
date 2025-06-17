package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByUserId(Long userId);

}

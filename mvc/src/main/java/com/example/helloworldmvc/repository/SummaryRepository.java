package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Summary;
import com.example.helloworldmvc.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Page<Summary> findAllByUserEmail(String userEmail, Pageable pageable);

}

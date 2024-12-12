package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Community;
import com.example.helloworldmvc.domain.User;
import com.example.helloworldmvc.domain.enums.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findAllByCommunityCategory(CommunityCategory category, Pageable pageable);

    Page<Community> findAllByUserId(Long userId, Pageable pageable);
}

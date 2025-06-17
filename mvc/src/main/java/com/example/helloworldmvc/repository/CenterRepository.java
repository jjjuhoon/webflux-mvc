package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Center;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CenterRepository extends JpaRepository<Center, Long> {
    Page<Center> findAll(Pageable pageable);

    @Query(value = "SELECT c.*, (6371 * acos(cos(radians(:latitude)) * cos(radians(c.latitude)) * cos(radians(c.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(c.latitude)))) AS distance " +
            "FROM center c " +
            "ORDER BY distance ASC",
            nativeQuery = true)
    Page<Center> findAllOrderByDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, Pageable pageable);
}
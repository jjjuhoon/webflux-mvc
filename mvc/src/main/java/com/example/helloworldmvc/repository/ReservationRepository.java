package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Counselor;
import com.example.helloworldmvc.domain.Summary;
import com.example.helloworldmvc.domain.mapping.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findAllByCounselorId(Long counselorId, Pageable pageable);

}

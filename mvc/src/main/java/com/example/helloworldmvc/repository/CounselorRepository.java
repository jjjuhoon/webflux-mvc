package com.example.helloworldmvc.repository;

import com.example.helloworldmvc.domain.Center;
import com.example.helloworldmvc.domain.Counselor;
import com.example.helloworldmvc.domain.mapping.CounselorLanguage;
import com.example.helloworldmvc.domain.mapping.UserLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    @Query("SELECT DISTINCT c FROM Counselor c JOIN c.counselorLanguageList l JOIN c.center ce WHERE l.language.id IN :userLanguageList AND ce.id = :centerId")
    Page<Counselor> findAllByCounselorLanguageList(List<Long> userLanguageList, Long centerId, Pageable pageable);
}

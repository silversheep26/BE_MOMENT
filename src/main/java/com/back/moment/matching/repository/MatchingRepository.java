package com.back.moment.matching.repository;

import com.back.moment.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    Matching findByBoardId(Long boardId);
}

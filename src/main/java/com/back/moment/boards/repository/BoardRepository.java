package com.back.moment.boards.repository;

import com.back.moment.boards.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}

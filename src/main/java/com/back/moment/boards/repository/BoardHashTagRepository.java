package com.back.moment.boards.repository;

import com.back.moment.boards.entity.BoardHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHashTagRepository extends JpaRepository<BoardHashTag, Long> {
    BoardHashTag findByHashTag(String hashTag);
}

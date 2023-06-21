package com.back.moment.matching.repository;

import com.back.moment.matching.dto.MatchApplyResponseDto;
import com.back.moment.matching.entity.MatchingApply;
import com.back.moment.users.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingApplyRepository extends JpaRepository<MatchingApply, Long> {

	List<MatchingApply> findAllByBoardId(Long boardId);

	List<MatchingApply> findAllByApplicant(Users users);

	@Query("select count(ma) from MatchingApply ma where ma.board.id = :boardId and ma.matchedCheck = false")
	int countAllMatchingWithFalse(Long boardId);

	MatchingApply findByBoardIdAndApplicantId(Long boardId, Long ApplicantId);

	@Query("select new com.back.moment.matching.dto.MatchApplyResponseDto(ma) from MatchingApply ma where ma.board.id = :boardId and ma.matchedCheck = false")
	List<MatchApplyResponseDto> findApplyWithFalse(@Param("boardId") Long boardId);
}

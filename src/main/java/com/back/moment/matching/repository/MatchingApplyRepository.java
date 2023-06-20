package com.back.moment.matching.repository;

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

	int countAllByBoardId(Long boardId);

	void deleteByBoardIdAndApplicantId(Long boardId, Long applicantId);

	//boolean existsByBoardIdAndApplicantId(Long boardId, Long ApplicantId);

	MatchingApply findByBoardIdAndApplicantId(Long boardId, Long ApplicantId);


	@Query("select m from MatchingApply m where m.board.id = :boardId and m.applicant.id <> :userId")
	List<MatchingApply> findAllMatchingApplyWhereIsNotUserId(@Param("boardId") Long boardId, @Param("userId") Long userId);

}

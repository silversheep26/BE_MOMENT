package com.back.moment.matching.service;

import com.back.moment.boards.entity.Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.matching.dto.MatchApplyResponseDto;
import com.back.moment.matching.dto.MatchingApplyBoardResponseDto;
import com.back.moment.matching.dto.MatchingBoardResponseDto;
import com.back.moment.matching.entity.Matching;
import com.back.moment.matching.entity.MatchingApply;
import com.back.moment.matching.repository.MatchingApplyRepository;
import com.back.moment.matching.repository.MatchingRepository;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {

	private final BoardRepository boardRepository;
	private final UsersRepository usersRepository;
	private final MatchingApplyRepository matchingApplyRepository;
	private final MatchingRepository matchingRepository;

	// 신청자 : 게시글 상세에서 확인 가능 (매칭 요청) 이미 매칭 요청 한 경우 매칭 취소 : 매칭 신청 버튼을 누르면 발생하는 이벤트
	public ResponseEntity<Void> matchApplyBoard(Long boardId, Users users) {
		Board board = existBoard(boardId);
		// board 작성자인 경우
		if (board.getUsers().getId().equals(users.getId())){  // 같으면 마이페이지 보내기
			throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);
		}
		// 이전에 매칭 신청 안 한 경우
		MatchingApply matchingApply = new MatchingApply(board, users);
//		if (matchingApplyRepository.countAllByBoardId(boardId) > 6) { // 최대 신청 인원 5 넘길 경우
//			throw new ApiException(ExceptionEnum.OVER_MATCHING_COUNT);
//		}
		// 이미 매칭요청을 했으면 , 매칭 취소 : db 에서 삭제 + board 에서 false 로 변경
		if (matchingApplyRepository.existsByBoardIdAndApplicantId(boardId, users.getId())) {
			int totalApplicantCnt = matchingApplyRepository.countAllByBoardId(boardId);
//			matchingApplyRepository.deleteByBoardIdAndApplicantId(boardId, users.getId());
			MatchingBoardResponseDto matchingBoardResponseDto = new MatchingBoardResponseDto(board,	totalApplicantCnt-1);
			matchingApplyRepository.delete(matchingApply);
		}

		matchingApplyRepository.save(matchingApply);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 수락자 : 마이페이지에서 수락하기 누르기 (매칭 수락) -> 보인다: 마이페이지 주인
	// 우선 마이페이지 게시글에 대한 매칭요청 리스트 -> 매칭요청 리스트중에 각 매칭요청마다
	// 해당 요청을 했던 유저정보가 이미 들어있을거잖아요?
	// matchingapply delete
	public ResponseEntity<Void> matchAcceptBoard(Long boardId, Long applyUserId, Users users) {
		Users applyUser = usersRepository.findById(applyUserId).orElseThrow(()->new ApiException(ExceptionEnum.NOT_FOUND_USER));
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new ApiException(ExceptionEnum.BAD_REQUEST));
		Matching matching = new Matching(boardId, applyUser, users);
		matchingRepository.save(matching);
		board.setMatching(true);
		List<MatchingApply> matchingApplyList = matchingApplyRepository.findAllMatchingApplyWhereIsNotUserId(
			boardId, applyUserId);
		matchingApplyRepository.deleteAll(matchingApplyList);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 해당 게시글에 매칭 요청 리스트
	// 매칭이 된 게시물이라면 , 매칭이 완료된 게시물이면 , 버튼을 보이지 않아야함
	// 매칭이 안된 게시물이면 , 매칭요청 리스트보기가 있어야함
	public ResponseEntity<List<MatchApplyResponseDto>> matchingApplyList(Long boardId, Users users) {
		Board board = boardRepository.findById(boardId)	.orElseThrow(() -> new ApiException(ExceptionEnum.BAD_REQUEST));
		usersRepository.findById(users.getId())
			.orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER));
		List<MatchApplyResponseDto> matchApplyResponseDtoList = new ArrayList<>();
		List<MatchingApply> matchingApplyList = matchingApplyRepository.findAllByBoardId(boardId);
		for (MatchingApply matchingApply : matchingApplyList) {
			matchApplyResponseDtoList.add(new MatchApplyResponseDto(matchingApply.getApplicant().getId(),
																	matchingApply.getApplicant().getNickName(),
																	matchingApply.getApplicant().getProfileImg()));
		}
		return ResponseEntity.ok(matchApplyResponseDtoList);
	}
	// 내가 받은 매칭 신청 리스트
    @Transactional(readOnly = true)
    public ResponseEntity<List<MatchingBoardResponseDto>> getMatchingList(Long hostId, Users users){
        usersRepository.findById(hostId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_MATCH_USERS)
        );

        List<Board> boardList = boardRepository.getBoardListByHostIdWithFetch(hostId);

        List<MatchingBoardResponseDto> matchingBoardResponseDtos = new ArrayList<>();
        for (Board board : boardList) {
            int totalApplicantCnt = matchingApplyRepository.countAllByBoardId(board.getId());
			MatchingBoardResponseDto matchingBoardResponseDto = new MatchingBoardResponseDto(board, totalApplicantCnt);
			matchingBoardResponseDtos.add(matchingBoardResponseDto);
        }
		return ResponseEntity.ok(matchingBoardResponseDtos);
    }

	// 내가 신청한 매칭 게시글
	// 매칭 중(isMatched = false)/ 매칭 완료
	@Transactional(readOnly = true)
	public ResponseEntity<List<MatchingApplyBoardResponseDto>> getMatchingApplyList(Users users) {
		List<MatchingApply> matchingApplyList = matchingApplyRepository.findAllByApplicant(users);
		List<Board> boardList = new ArrayList<>();
		List<MatchingApplyBoardResponseDto> matchingBoardResponseDtos = new ArrayList<>();
		for (MatchingApply matchingApply : matchingApplyList) {
			MatchingApplyBoardResponseDto matchingBoardResponseDto = new MatchingApplyBoardResponseDto(
				matchingApply.getBoard());
			matchingBoardResponseDtos.add(matchingBoardResponseDto);
		}

		return ResponseEntity.ok(matchingBoardResponseDtos);
	}

	public Board existBoard(Long boardId){
		return boardRepository.findExistBoard(boardId).orElseThrow(
			() -> new ApiException(ExceptionEnum.NOT_FOUND_POST)
		);
	}
}


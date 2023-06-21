package com.back.moment.matching.controller;

import com.back.moment.matching.dto.MatchAcceptResponseDto;
import com.back.moment.matching.dto.MatchApplyResponseDto;
import com.back.moment.matching.dto.MatchingApplyBoardResponseDto;
import com.back.moment.matching.dto.MatchingBoardResponseDto;
import com.back.moment.matching.service.MatchingService;
import com.back.moment.sse.NotificationService;
import com.back.moment.users.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("match")
public class MatchingController {

	private final MatchingService matchingService;

	// 게시글 상세 조회 매칭 신청
	@PostMapping("/apply/{boardId}")
	public ResponseEntity<Void> matchApplyBoard(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return matchingService.matchApplyBoard(boardId, userDetails.getUsers());
	}

	// 매칭 수락
	@PostMapping("/accept/{boardId}/{applyUserId}")
	public ResponseEntity<MatchAcceptResponseDto> matchAcceptBoard(@PathVariable Long boardId, @PathVariable Long applyUserId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return matchingService.matchAcceptBoard(boardId, applyUserId, userDetails.getUsers());
	}

	// 매칭 요청 리스트 보기
	@GetMapping("/apply-list/{boardId}")
	public ResponseEntity<List<MatchApplyResponseDto>> matchingApplyList(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		return matchingService.matchingApplyList(boardId, userDetails.getUsers());
	}

	// 마이페이지에서 매칭 리스트 보기 : 내가 받은 매칭 신청 게시글 보기
	@GetMapping("/accept-list")
	public ResponseEntity<List<MatchingBoardResponseDto>> getMatchedList(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return matchingService.getMatchedList(userDetails.getUsers());
	}

	// 마이페이지에서 매칭 리스트 보기 : 내가 신청한 매칭 게시글 보기
	@GetMapping("/apply-list")
	public ResponseEntity<List<MatchingApplyBoardResponseDto>> getMatchingApplyList(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return matchingService.getMatchingApplyList(userDetails.getUsers());
	}

	@PostMapping("/delete/{boardId}/{applyUserId}")
	public ResponseEntity<Void> deleteMatchingApply(@PathVariable Long boardId,
													@PathVariable Long applyUserId,
													@AuthenticationPrincipal UserDetailsImpl userDetails){
		return matchingService.deleteMatchingApply(boardId, applyUserId, userDetails.getUsers());
	}

	@DeleteMapping("/delete/matched/{boardId}")
	public ResponseEntity<Void> deleteMatching(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails){
		return matchingService.deleteMatching(boardId, userDetails.getUsers());
	}
}

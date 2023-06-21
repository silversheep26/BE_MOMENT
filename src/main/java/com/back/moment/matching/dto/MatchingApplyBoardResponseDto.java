package com.back.moment.matching.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.global.dto.TagResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchingApplyBoardResponseDto {
	private Long boardId;
	private String title;
	private String role;
	private String nickName;
	private Long boardHostId;
	private String profileImgUrl;
	private String location;
	private String pay;
	private String deadLine;
	private String boardImgUrl;
	private LocalDateTime createdTime;
	private List<TagResponseDto> tag_boardList;
	private List<MatchApplyResponseDto> matchApplyDtoList = new ArrayList<>();
	private boolean alreadyMatch;
	private boolean matchingWith;
	private int totalApplicantCnt;

	public MatchingApplyBoardResponseDto(Board board, boolean alreadyMatch, boolean matchingWith, int totalApplicantCnt) {
		this.boardId = board.getId();
		this.title = board.getTitle();
		this.role = board.getUsers().getRole();
		this.nickName = board.getUsers().getNickName();
		this.boardHostId = board.getUsers().getId();
		this.profileImgUrl = board.getUsers().getProfileImg();
		this.location = board.getLocation();
		this.pay = board.getPay();
		this.deadLine = board.getDeadLine();
		this.boardImgUrl = board.getBoardImgUrl();
		this.createdTime = board.getCreatedAt();
		this.tag_boardList = board.getTagListWithWell();
		this.alreadyMatch = alreadyMatch;
		this.matchingWith = matchingWith;
		this.totalApplicantCnt = totalApplicantCnt;
	}

}

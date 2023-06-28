package com.back.moment.matching.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.global.dto.TagResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchingBoardResponseDto {
	private Long boardId;
	private String title;
	private String role;
	private String nickName;
	private String profileImgUrl;
	private String location;
	private String pay;
	private String deadLine;
	private String boardImgUrl;
	private LocalDateTime createdTime;
	private List<TagResponseDto> tag_boardList;
	private int totalApplicantCnt;
	private Boolean matching;
	private String whoMatch;
	private Long whoMatchId;


	public MatchingBoardResponseDto(Board board, int totalApplicantCnt, String whoMatch, Long whoMatchId) {
		this.boardId = board.getId();
		this.title = board.getTitle();
		this.role = board.getUsers().getRole();
		this.nickName = board.getUsers().getNickName();
		this.profileImgUrl = board.getUsers().getProfileImg();
		this.location = board.getLocation();
		this.pay = board.getPay();
		this.deadLine = board.getDeadLine();
		this.boardImgUrl = board.getBoardImgUrl();
		this.createdTime = board.getCreatedAt().plusHours(9);
		this.tag_boardList = board.getTagListWithWell();
		this.totalApplicantCnt = totalApplicantCnt;
		this.matching = board.getMatching();
		this.whoMatch = whoMatch;
		this.whoMatchId = whoMatchId;
	}
}

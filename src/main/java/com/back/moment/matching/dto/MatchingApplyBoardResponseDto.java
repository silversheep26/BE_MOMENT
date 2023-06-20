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
	private String profileImgUrl;
	private String location;
	private String boardImgUrl;
	private LocalDateTime createdTime;
	private boolean isMatched;

	private List<TagResponseDto> tag_boardList;
	private List<MatchApplyResponseDto> matchApplyDtoList = new ArrayList<>();

	public MatchingApplyBoardResponseDto(Board board) {
		this.boardId = board.getId();
		this.title = board.getTitle();
		this.role = board.getUsers().getRole();
		this.nickName = board.getUsers().getNickName();
		this.profileImgUrl = board.getUsers().getProfileImg();
		this.location = board.getLocation();
		this.boardImgUrl = board.getBoardImgUrl();
		this.createdTime = board.getCreatedAt();
		this.isMatched = board.getMatching();
		this.tag_boardList = board.getTagListWithWell();
	}

}

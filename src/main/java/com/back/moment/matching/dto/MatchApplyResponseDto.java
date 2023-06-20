package com.back.moment.matching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchApplyResponseDto {
	private Long boardId;
	private Long userId;
	private String userNickName;
	private String userProfileImg;

	public MatchApplyResponseDto(Long boardId, Long userId, String userNickName, String userProfileImg) {
		this.boardId = boardId;
		this.userId = userId;
		this.userNickName = userNickName;
		this.userProfileImg = userProfileImg;
	}
}


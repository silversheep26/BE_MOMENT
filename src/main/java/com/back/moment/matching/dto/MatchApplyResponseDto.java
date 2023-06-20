package com.back.moment.matching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchApplyResponseDto {
	private Long userId;
	private String userNickName;
	private String userProfileImg;

	public MatchApplyResponseDto(Long userId, String userNickName, String userProfileImg) {
		this.userId = userId;
		this.userNickName = userNickName;
		this.userProfileImg = userProfileImg;
	}
}


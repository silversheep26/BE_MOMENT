package com.back.moment.matching.dto;

import com.back.moment.matching.entity.MatchingApply;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchApplyResponseDto {
	private Long boardId;
	private Long userId;
	private String userNickName;
	private String userProfileImg;

	public MatchApplyResponseDto(MatchingApply matchingApply) {
		this.boardId = matchingApply.getBoard().getId();
		this.userId = matchingApply.getApplicant().getId();
		this.userNickName = matchingApply.getApplicant().getNickName();
		this.userProfileImg = matchingApply.getApplicant().getProfileImg();
	}
}


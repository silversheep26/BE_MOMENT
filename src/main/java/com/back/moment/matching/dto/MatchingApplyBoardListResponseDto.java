package com.back.moment.matching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MatchingApplyBoardListResponseDto {
    List<MatchingApplyBoardResponseDto> matchingApplyBoardResponseDtoList;
    int totalCnt;

    public MatchingApplyBoardListResponseDto(List<MatchingApplyBoardResponseDto> matchingApplyBoardResponseDtoList, int totalCnt) {
        this.matchingApplyBoardResponseDtoList = matchingApplyBoardResponseDtoList;
        this.totalCnt = totalCnt;
    }
}

package com.back.moment.matching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MatchingBoardListResponseDto {
    List<MatchingBoardResponseDto> matchingBoardResponseDtoList;
    int totalCnt;

    public MatchingBoardListResponseDto(List<MatchingBoardResponseDto> matchingBoardResponseDtoList, int totalCnt) {
        this.matchingBoardResponseDtoList = matchingBoardResponseDtoList;
        this.totalCnt = totalCnt;
    }
}

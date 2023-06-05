package com.back.moment.mainPage.dto;

import com.back.moment.boards.dto.MyPageBoardListResponseDto;
import com.back.moment.users.dto.ForMainResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AfterLogInResponseDto {
    private List<ForMainResponseDto> eachRoleUsersList;
    private List<MyPageBoardListResponseDto> boardList;

    public AfterLogInResponseDto(List<ForMainResponseDto> eachRoleUsersList, List<MyPageBoardListResponseDto> boardList) {
        this.eachRoleUsersList = eachRoleUsersList;
        this.boardList = boardList;
    }
}

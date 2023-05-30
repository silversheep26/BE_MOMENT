package com.back.moment.mainPage.dto;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.users.dto.ForMainResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AfterLogInResponseDto {
    private List<ForMainResponseDto> eachRoleUsersList;
    private List<BoardListResponseDto> boardList;

    public AfterLogInResponseDto(List<ForMainResponseDto> eachRoleUsersList, List<BoardListResponseDto> boardList) {
        this.eachRoleUsersList = eachRoleUsersList;
        this.boardList = boardList;
    }
}

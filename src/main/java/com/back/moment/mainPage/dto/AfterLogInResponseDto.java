package com.back.moment.mainPage.dto;

import com.back.moment.users.dto.ForMainResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AfterLogInResponseDto {
    private List<ForMainResponseDto> eachRoleUsersList;

    public AfterLogInResponseDto(List<ForMainResponseDto> eachRoleUsersList) {
        this.eachRoleUsersList = eachRoleUsersList;
    }
}

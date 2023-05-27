package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDetailResponseDto {
    private String profileUrl;
    private String nickName;
    private String title;
    private String contents;
    private RoleEnum role;
    private List<String> tag_boardList;

    public BoardDetailResponseDto(Board board){
        this.profileUrl = board.getBoardImgUrl();
        this.nickName = board.getUsers().getNickName();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.role = board.getUsers().getRole();
        this.tag_boardList = board.getTagListWithWell();
    }
}

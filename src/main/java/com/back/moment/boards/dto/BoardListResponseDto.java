package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.Tag_Board;
import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardListResponseDto {
    private  String title;
    private  String role;
    private  String nickName;
    private  String boardImgUrl;
    private  LocalDate createdTime;
    private  List<String> tag_boardList;

    public BoardListResponseDto(Board board) {
        this.title = board.getTitle();
        this.role = board.getUsers().getRole();
        this.nickName = board.getUsers().getNickName();
        this.boardImgUrl = board.getBoardImgUrl();
        this.createdTime = board.getCreatedAt();
        this.tag_boardList = board.getTagList();
    }
}

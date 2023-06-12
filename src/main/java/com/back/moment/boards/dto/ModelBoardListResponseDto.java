package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.global.dto.TagResponseDto;
import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ModelBoardListResponseDto {
    private Long boardId;
    private  String title;
    private RoleEnum role;
    private  String nickName;
    private  String boardImgUrl;
    private LocalDateTime createdTime;
    private List<TagResponseDto> tag_boardList;

    public ModelBoardListResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.role = board.getUsers().getRole();
        this.nickName = board.getUsers().getNickName();
        this.boardImgUrl = board.getBoardImgUrl();
        this.createdTime = board.getCreatedAt().plusHours(9L);
        this.tag_boardList = board.getTagListWithWell();
    }
}

package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.global.dto.TagResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardSearchListResponseDto {
    private Long boardId;
    private  String title;
    private  String role;
    private  String nickName;
    private String deadLine;
    private String location;
    private String pay;
    private  String boardImgUrl;
    private String hostProfileUrl;
    private LocalDateTime createdTime;
    private List<TagResponseDto> tag_boardList;
    private boolean checkMatched;

    public BoardSearchListResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.role = board.getUsers().getRole();
        this.nickName = board.getUsers().getNickName();
        this.deadLine = board.getDeadLine();
        this.location = board.getLocation();
        this.pay = board.getPay();
        this.boardImgUrl = board.getBoardImgUrl();
        this.hostProfileUrl = board.getUsers().getProfileImg();
        this.createdTime = board.getCreatedAt().plusHours(9);
        this.tag_boardList = board.getTagListWithWell();
        this.checkMatched = board.getMatching();
    }
}

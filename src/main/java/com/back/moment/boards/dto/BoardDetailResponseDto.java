package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.global.dto.TagResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import java.util.Comparator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class BoardDetailResponseDto {
    private Long hostId;
    private Long boardId;
    private String nickName;
    private String title;
    private String content;
    private String location;
    private String pay;
    private String apply;
    private String deadLine;
    private String role;
    private List<TagResponseDto> tag_boardList;
    private String profileUrl;
    private String boardImgUrl;
    private List<OnlyPhotoResponseDto> feedImgUrl;
    private boolean checkApply;
    private boolean checkMatched;

    public BoardDetailResponseDto(Board board, boolean checkApply, boolean checkMatched){
        this.hostId = board.getUsers().getId();
        this.boardId = board.getId();
        this.nickName = board.getUsers().getNickName();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.location = board.getLocation();
        this.pay = board.getPay();
        this.apply = board.getApply();
        this.deadLine = board.getDeadLine();
        this.role = board.getUsers().getRole();
        this.tag_boardList = board.getTagListWithWell();
        this.profileUrl = board.getUsers().getProfileImg();
        this.boardImgUrl = board.getBoardImgUrl();
        this.feedImgUrl = board.getUsers().getPhotoList().stream()
            .map(OnlyPhotoResponseDto::new)
            .sorted(Comparator.comparingInt(OnlyPhotoResponseDto::getLoveCnt).reversed())
            .limit(6)
            .toList();
        this.checkApply = checkApply;
        this.checkMatched = checkMatched;
    }
}

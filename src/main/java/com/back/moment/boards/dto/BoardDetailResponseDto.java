package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.RoleEnum;
import java.util.Comparator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDetailResponseDto {
    private Long hostId;
    private String nickName;
    private String title;
    private String contents;
    private RoleEnum role;
    private List<String> tag_boardList;
    private String profileUrl;
    private List<OnlyPhotoResponseDto> feedImgUrl;

    public BoardDetailResponseDto(Board board){
        this.hostId = board.getUsers().getId();
        this.nickName = board.getUsers().getNickName();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.role = board.getUsers().getRole();
        this.tag_boardList = board.getTagListWithWell();
        this.profileUrl = board.getUsers().getProfileImg();
        this.feedImgUrl = board.getUsers().getPhotoList().stream()
            .map(OnlyPhotoResponseDto::new)
            .sorted(Comparator.comparingInt(OnlyPhotoResponseDto::getLoveCnt).reversed())
            .limit(6)
            .toList();
    }
}

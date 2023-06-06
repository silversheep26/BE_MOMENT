package com.back.moment.users.dto;

import com.back.moment.boards.dto.MyPageBoardListResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyPageResponseDto {
    private String profileUrl;
    private String nickName;
    private Long hostId;
    private RoleEnum role;
    private int boardCnt;
    private int totalPhotoLoveCnt;
    private boolean checkKakaoId = false;
    //    private int recommendCnt;
    private List<MyPageBoardListResponseDto> boardList;
    private List<OnlyPhotoResponseDto> photoList;

    public MyPageResponseDto(Users users, List<MyPageBoardListResponseDto> boardList, List<OnlyPhotoResponseDto> photoList){
        this.profileUrl = users.getProfileImg();
        this.nickName = users.getNickName();
        this.hostId = users.getId();
        this.role = users.getRole();
        this.boardCnt = users.getBoardList().size();
        this.totalPhotoLoveCnt = users.getTotalLoveCnt();
//        this.recommendCnt = users.getRecommendedList().size();
        this.boardList = boardList;
        this.photoList = photoList;
    }
}

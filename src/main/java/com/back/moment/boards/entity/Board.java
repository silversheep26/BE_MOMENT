package com.back.moment.boards.entity;

import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.boards.dto.UpdateBoardRequestDto;
import com.back.moment.global.dto.TagResponseDto;
import com.back.moment.common.TimeStamped;
import com.back.moment.matching.entity.MatchingApply;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Column
    private String location;

    @Column
    private String pay;

    @Column
    private String apply;

    @Column
    private String deadLine;

    @Column
    private String boardImgUrl;

    @Column
    private String role;

    @Column
    private Boolean matching = false;// 매칭 여부

    @Column
    private Boolean matchingFull = false;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<MatchingApply> matchingApplyList = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Tag_Board> tag_boardList = new ArrayList<>();

    public Board(Users users, String title, String content, String location, String pay, String apply, String deadLine, String boardImgUrl, String role) {
        this.users = users;
        this.title = title;
        this.content = content;
        this.location = location;
        this.pay = pay;
        this.apply = apply;
        this.deadLine = deadLine;
        this.boardImgUrl = boardImgUrl;
        this.role = role;
    }

    public void saveBoard(BoardRequestDto boardRequestDto, Users users){
        this.users = users;
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
        this.location = boardRequestDto.getLocation();
        this.pay = boardRequestDto.getPay();
        this.apply = boardRequestDto.getApply();
        this.deadLine = boardRequestDto.getDeadLine();
        this.role = users.getRole();
    }

    public List<String> getTagList(){
        return tag_boardList.stream()
                .map(tag_board -> tag_board.getBoardHashTag().getHashTag())
                .collect(Collectors.toList());
    }

    public List<TagResponseDto> getTagListWithWell(){
        List<TagResponseDto> tagList = new ArrayList<>();
        for(Tag_Board tag_board : tag_boardList){
            String tag = tag_board.getBoardHashTag().getHashTag();
            TagResponseDto tagResponseDto = new TagResponseDto(tag_board.getBoardHashTag().getId(), "#" + tag);
            tagList.add(tagResponseDto);
        }
        return tagList;
    }

    public void updateBoard(UpdateBoardRequestDto updateBoardRequestDto) {
        this.title = isNotNullOrEmpty(updateBoardRequestDto.getTitle()) ? updateBoardRequestDto.getTitle() : this.getTitle();
        this.content = isNotNullOrEmpty(updateBoardRequestDto.getContent()) ? updateBoardRequestDto.getContent() : this.getContent();
        this.location = isNotNullOrEmpty(updateBoardRequestDto.getLocation()) ? updateBoardRequestDto.getLocation() : this.getLocation();
        this.pay = isNotNullOrEmpty(updateBoardRequestDto.getPay()) ? updateBoardRequestDto.getPay() : this.getPay();
        this.apply = isNotNullOrEmpty(updateBoardRequestDto.getApply()) ? updateBoardRequestDto.getApply() : this.getApply();
        this.deadLine = isNotNullOrEmpty(updateBoardRequestDto.getDeadLine()) ? updateBoardRequestDto.getDeadLine() : this.getDeadLine();
    }

    private boolean isNotNullOrEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}

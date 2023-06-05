package com.back.moment.boards.entity;

import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.common.TimeStamped;
import com.back.moment.users.entity.RoleEnum;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String pay;

    @Column(nullable = false)
    private String apply;

    @Column(nullable = false)
    private String deadLine;

    @Column
    private String boardImgUrl;

    @Column
    private RoleEnum role;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Tag_Board> tag_boardList = new ArrayList<>();

    public Board(Users users, String title, String location, String pay, String apply, String deadLine, String boardImgUrl, RoleEnum role) {
        this.users = users;
        this.title = title;
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
        this.location = boardRequestDto.getLocation();
        this.pay = boardRequestDto.getPay();
        this.apply = boardRequestDto.getApply();
        this.deadLine = boardRequestDto.getDeadLine();
        this.role = users.getRole();
    }

    public List<String> getTagList(){
        return tag_boardList.stream()
                .map(tag_board -> tag_board.getLocationTag().getLocation())
                .collect(Collectors.toList());
    }

    public List<String> getTagListWithWell(){
        List<String> tagList = new ArrayList<>();
        for(Tag_Board tag_board : tag_boardList){
            String tag = tag_board.getLocationTag().getLocation();
            tagList.add("#" + tag);
        }
        return tagList;
    }
}

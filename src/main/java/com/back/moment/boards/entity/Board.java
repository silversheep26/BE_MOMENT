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
    private String contents;

    @Column
    private String boardImgUrl;

    @Column
    private RoleEnum role;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Tag_Board> tag_boardList = new ArrayList<>();

    private Board(Users users, String title, String contents, RoleEnum role) {
        this.users = users;
        this.title = title;
        this.contents = contents;
        this.role = role;
    }

    public void saveBoard(BoardRequestDto boardRequestDto, Users users){
        this.users = users;
        this.title = boardRequestDto.getTitle();
        this.contents = boardRequestDto.getContents();
        this.role = users.getRole();
    }

    public List<String> getTagList(){
        List<String> tagList = new ArrayList<>();
        for(Tag_Board tag_board : tag_boardList){
            String tag = tag_board.getLocationTag().getLocation();
            tagList.add(tag);
        }
        return tagList;
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

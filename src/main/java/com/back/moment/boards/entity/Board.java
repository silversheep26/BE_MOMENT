package com.back.moment.boards.entity;

import com.back.moment.boards.dto.BoardRequestDto;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board {
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

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Tag_Board> tag_boardList = new ArrayList<>();

    private Board(Users users, String title, String contents) {
        this.users = users;
        this.title = title;
        this.contents = contents;
    }

    public void saveBoard(BoardRequestDto boardRequestDto, Users users){
        this.users = users;
        this.title = boardRequestDto.getTitle();
        this.contents = boardRequestDto.getContents();
    }
}

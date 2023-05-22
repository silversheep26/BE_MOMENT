package com.back.moment.boards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Tag_Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationTag_id")
    @JsonIgnore
    private LocationTag locationTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Board_id")
    @JsonIgnore
    private Board board;

    public Tag_Board(LocationTag locationTag, Board board) {
        this.locationTag = locationTag;
        this.board = board;
    }
}

package com.back.moment.boards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    private String title;
    private String content;
    private String location;
    private String pay;
    private String apply;
    private String deadLine;
    private List<String> boardHashTag;

    public void setBoardHashTag(List<String> boardHashTag){
        if(boardHashTag != null){
            this.boardHashTag = boardHashTag.stream()
                    .filter(tag -> tag.startsWith("#"))
                    .collect(Collectors.toList());
        } else {
            this.boardHashTag = null;
        }
    }
}

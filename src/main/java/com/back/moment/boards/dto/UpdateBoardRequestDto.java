package com.back.moment.boards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBoardRequestDto {
    private String title;
    private String content;
    private String location;
    private String pay;
    private String apply;
    private String deadLine;
}

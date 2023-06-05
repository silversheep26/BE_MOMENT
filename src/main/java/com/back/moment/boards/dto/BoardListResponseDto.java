package com.back.moment.boards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
public class BoardListResponseDto {
    Page<ModelBoardListResponseDto> modelBoard;
    Page<PhotographerBoardListResponseDto> photographerBoard;

    public BoardListResponseDto(Page<ModelBoardListResponseDto> modelBoard, Page<PhotographerBoardListResponseDto> photographerBoard) {
        this.modelBoard = modelBoard;
        this.photographerBoard = photographerBoard;
    }
}

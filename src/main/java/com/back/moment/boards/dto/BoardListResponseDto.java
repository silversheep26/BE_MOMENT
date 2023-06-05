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
    boolean modelHasMorePage;
    boolean photographerHasMorePage;
    int modelCurrentPage;
    int modelTotalPages;
    int photographerCurrentPage;
    int photographerTotalPages;

    public BoardListResponseDto(Page<ModelBoardListResponseDto> modelBoard,
                                Page<PhotographerBoardListResponseDto> photographerBoard,
                                boolean modelHasMorePage,
                                boolean photographerHasMorePage,
                                int modelCurrentPage,
                                int modelTotalPages,
                                int photographerCurrentPage,
                                int photographerTotalPages) {
        this.modelBoard = modelBoard;
        this.photographerBoard = photographerBoard;
        this.modelHasMorePage = modelHasMorePage;
        this.photographerHasMorePage = photographerHasMorePage;
        this.modelCurrentPage = modelCurrentPage;
        this.modelTotalPages = modelTotalPages;
        this.photographerCurrentPage = photographerCurrentPage;
        this.photographerTotalPages = photographerTotalPages;
    }
}

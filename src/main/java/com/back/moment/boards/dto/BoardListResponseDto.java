package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.boards.entity.Tag_Board;
import com.back.moment.users.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

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

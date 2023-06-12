package com.back.moment.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagResponseDto {
    private Long tagId;
    private String tag;

    public TagResponseDto(Long tagId, String tag) {
        this.tagId = tagId;
        this.tag = tag;
    }
}

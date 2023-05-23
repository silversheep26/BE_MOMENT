package com.back.moment.boards.dto;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardRequestDto {
    private String title;
    private String contents;
    private List<String> locationTags;

    public void setLocationTags(List<String> locationTags){
        if(locationTags != null){
            this.locationTags = locationTags.stream()
                    .filter(tag -> tag.startsWith("#"))
                    .collect(Collectors.toList());
        } else {
            this.locationTags = null;
        }
    }
}

package com.back.moment.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class FeedRequestDto {
    private String content;
    private List<String> photoHashTag;

    public void setPhotoHashTag(List<String> photoHashTag){
        if(photoHashTag != null){
            this.photoHashTag = photoHashTag.stream()
                    .filter(tag -> tag.startsWith("#"))
                    .collect(Collectors.toList());
        } else {
            this.photoHashTag = null;
        }
    }
}

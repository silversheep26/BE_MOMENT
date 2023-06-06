package com.back.moment.photos.entity;

import com.back.moment.boards.entity.Tag_Board;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PhotoHashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hashTag;

    @OneToMany(mappedBy = "photoHashTag", cascade = CascadeType.REMOVE)
    private List<Tag_Photo> tag_photoList = new ArrayList<>();

    public PhotoHashTag(String hashTag){
        this.hashTag = hashTag;
    }
}

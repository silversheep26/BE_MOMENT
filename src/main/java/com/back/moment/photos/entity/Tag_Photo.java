package com.back.moment.photos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Tag_Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private PhotoHashTag photoHashTag;

    @ManyToOne
    @JsonIgnore
    private Photo photo;

    public Tag_Photo(PhotoHashTag photoHashTag, Photo photo){
        this.photoHashTag = photoHashTag;
        this.photo = photo;
    }
}

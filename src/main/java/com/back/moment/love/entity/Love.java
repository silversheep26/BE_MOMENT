package com.back.moment.love.entity;

import com.back.moment.boards.entity.Board;
import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Love {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    @JsonIgnore
    private Photo photo;

    @Column
    private boolean loveChk;

    public Love(Users users, Photo photo) {
        this.users = users;
        this.photo = photo;
    }

    public void updateLoveCheck(boolean loveChk){
        this.loveChk = loveChk;
    }
}

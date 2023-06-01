package com.back.moment.love.entity;

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
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Photo photo;

    public Love(Users users, Photo photo) {
        this.users = users;
        this.photo = photo;
    }
}

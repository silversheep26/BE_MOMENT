package com.back.moment.love.entity;

import com.back.moment.common.TimeStamped;
import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Love extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Photo photo;

    public Love(Users users, Photo photo) {
        this.users = users;
        this.photo = photo;
    }
}

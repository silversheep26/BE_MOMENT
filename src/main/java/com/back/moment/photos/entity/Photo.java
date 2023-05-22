package com.back.moment.photos.entity;

import com.back.moment.love.entity.Love;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
    private List<Love> loveList = new ArrayList<>();
}

package com.back.moment.photos.entity;

import com.back.moment.love.entity.Love;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;

    @Column(nullable = false)
    private String imagUrl;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
    private List<Love> loveList = new ArrayList<>();

    public Photo(Users users, String imagUrl) {
        this.users = users;
        this.imagUrl = imagUrl;
    }
}

package com.back.moment.users.entity;

import com.back.moment.boards.entity.Board;
import com.back.moment.love.entity.Love;
import com.back.moment.recommend.entity.Recommend;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SexEnum sex;

    @Column
    private String profileImg;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Love> loveList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(mappedBy = "recommender", cascade = CascadeType.ALL)
    private List<Recommend> recommenderList = new ArrayList<>();

    @OneToMany(mappedBy = "recommended", cascade = CascadeType.ALL)
    private List<Recommend> recommendedList = new ArrayList<>();

    private Users(String email, String nickName, String password, SexEnum sex, String profileImg, RoleEnum role){
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.sex = sex;
        this.profileImg = profileImg;
        this.role = role;
    }
}

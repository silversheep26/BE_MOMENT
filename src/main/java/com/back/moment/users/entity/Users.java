package com.back.moment.users.entity;

import com.back.moment.boards.entity.Board;
import com.back.moment.love.entity.Love;
import com.back.moment.photos.entity.Photo;
import com.back.moment.users.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
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

    @Column
    private Long kakaoId;

    @Column(nullable = false, unique = true)
    private String email;  // 이메일

    @Column(nullable = false)
    private String nickName;  // 닉네임

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(nullable = false)
    //@Enumerated(value = EnumType.STRING)
    private String gender;  // 성별

    @Column
    private String profileImg;  // 프로필 사진

    @ColumnDefault("0")
    private int totalLoveCnt;

    @Column
    //@Enumerated(value = EnumType.STRING)
    private RoleEnum role;  // 모델 또는 작가

//    @Column(nullable = false)
//    private boolean userDelete = false;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Photo> photoList = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Love> loveList = new ArrayList<>();  //좋아요(내가 좋아요 누른 사진 목록)

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Board> boardList = new ArrayList<>();

//    @OneToMany(mappedBy = "recommender", cascade = CascadeType.ALL)
//    private List<Recommend> recommenderList = new ArrayList<>();  // 추천 하는 사람
//
//    @OneToMany(mappedBy = "recommended", cascade = CascadeType.ALL)
//    private List<Recommend> recommendedList = new ArrayList<>();  // 추천 받은 사람
//
//    @ColumnDefault("0")
//    private int recommendCnt;

    private Users(String email, String nickName, String password, String gender, String profileImg, RoleEnum role){
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.gender = gender;
        this.profileImg = profileImg;
        this.role = role;
    }

    public Users(String email, String nickName, String password, String gender, String profileImg){
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.gender = gender;
        this.profileImg = profileImg;
    }

    public void saveUsers(SignupRequestDto requestDto, String password, String gender, RoleEnum role) {
        this.email = requestDto.getEmail();
        this.password = password;
        this.nickName = requestDto.getNickName();
        this.gender = gender;
        this.role = role;
    }

    public void updateUsers(String nickName, String profileUrl, String password, RoleEnum role){
        this.nickName = nickName;
        this.profileImg = profileUrl;
        this.password = password;
        this.role = role;
    }

    public Users kakaoIdUpdate(Long kakaoId){
        this.kakaoId=kakaoId;
        return this;
    }


//    public void deleteUsers() {
//        this.userDelete = true;
//    }

}

package com.back.moment.photos.entity;

import com.back.moment.love.entity.Love;
import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column
    private String contents = "사진 한줄 요약";

    @ColumnDefault("0")
    private int loveCnt;


    public Photo(Users users, String imagUrl) {
        this.users = users;
        this.imagUrl = imagUrl;
    }

    public void updateContents(String contents){
        this.contents = contents;
    }
}

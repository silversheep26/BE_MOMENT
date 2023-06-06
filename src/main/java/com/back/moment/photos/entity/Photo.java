package com.back.moment.photos.entity;

import com.back.moment.boards.entity.Tag_Board;
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
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "photo", cascade = CascadeType.REMOVE)
    private List<Love> loveList = new ArrayList<>();

    @Column
    private String contents = "사진 한줄 요약";

    @ColumnDefault("0")
    private int loveCnt;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.REMOVE)
    private List<Tag_Photo> tag_photoList = new ArrayList<>();


    public Photo(Users users, String imagUrl) {
        this.users = users;
        this.imagUrl = imagUrl;
    }

    public void updateContents(String contents){
        this.contents = contents;
    }

    public List<String> getTagList(){
        return tag_photoList.stream()
                .map(tag_board -> tag_board.getPhotoHashTag().getHashTag())
                .collect(Collectors.toList());
    }

    public List<String> getTagListWithWell(){
        List<String> tagList = new ArrayList<>();
        for(Tag_Photo tag_photo : tag_photoList){
            String tag = tag_photo.getPhotoHashTag().getHashTag();
            tagList.add("#" + tag);
        }
        return tagList;
    }
}

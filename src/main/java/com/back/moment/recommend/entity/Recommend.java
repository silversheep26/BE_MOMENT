package com.back.moment.recommend.entity;

import com.back.moment.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recommender_id")
    @JsonIgnore
    private Users recommender;

    @ManyToOne
    @JoinColumn(name = "recommended_id")
    @JsonIgnore
    private Users recommended;

    public Recommend(Users recommender, Users recommended) {
        this.recommender = recommender;
        this.recommended = recommended;
    }
}

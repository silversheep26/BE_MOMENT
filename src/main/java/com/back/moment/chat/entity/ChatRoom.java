package com.back.moment.chat.entity;

import com.back.moment.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userA;
    private Long userB;
    private ChatRoom(Long userA, Long userB) {
        this.userA = userA;
        this.userB = userB;
    }
    public static ChatRoom of(Long userA,Long userB){
        return new ChatRoom(userA,userB);
    }
}

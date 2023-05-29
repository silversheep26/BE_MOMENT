package com.back.moment.chat.entity;

import com.back.moment.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ChatRoom{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userOne_id")
    private Users userOne;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userTwo_id")
    private Users userTwo;
    private Boolean canUserOneSee = true; // 유저1이 이 채팅방을 지웠는지 여부 확인
    private Boolean canUserTwoSee = true; // 유저2가 이 채팅방을 지웠는지 여부 확인
    @Version
    private LocalDateTime lastMessageAt; // 가장 최근에 채팅이 생성된 시간을 나타냄
    private ChatRoom(Users userOne , Users userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
    }
    public static ChatRoom of(Users user1,Users user2){
        return new ChatRoom(user1,user2);
    }

    public void updateCanUserOneSee(){
        this.canUserOneSee = true;
    }
    public void updateCanUserTwoSee(){
        this.canUserTwoSee = true;
    }
    public void updateCanNotUserOneSee(){
        this.canUserOneSee = false;
    }
    public void updateCanNotUserTwoSee(){
        this.canUserTwoSee = false;
    }
    public void updateLastMessageAt(LocalDateTime localDateTime){
        this.lastMessageAt = localDateTime;
    }
}

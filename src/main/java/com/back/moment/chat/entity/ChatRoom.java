package com.back.moment.chat.entity;

import com.back.moment.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ChatRoom{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Users host;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Users guest;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime hostEntryTime; // 5/28 20시 10분 -> 6/3 20시 50분
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime guestEntryTime; // 5/28 20시 10분
    private ChatRoom(Users host , Users guest,LocalDateTime time) {
        this.host = host;
        this.guest = guest;
        this.hostEntryTime = time;
        this.guestEntryTime = time;
    }
    public static ChatRoom of(Users host,Users guest,LocalDateTime time){
        return new ChatRoom(host,guest,time);
    }
    public void updateHostEntryTime(LocalDateTime time){
        this.hostEntryTime = time;
    }
    public void updateGuestEntryTime(LocalDateTime time){
        this.guestEntryTime = time;
    }
}

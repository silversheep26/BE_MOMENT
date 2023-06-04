package com.back.moment.chat.repository;

import com.back.moment.chat.entity.ChatRoom;
import com.back.moment.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    // UserA 또는 UserB가 있는 채팅방을 모두 반환 ( 내 채팅목록같은 개념 ) , 수정이 필요할 수 있다.
    List<ChatRoom> findAllByHostOrGuest(Users host,Users guest);
    // 해당 유저 둘간의 채팅방을 반환 , 무조건 한개 또는 0개일 것
    @Query("select c from ChatRoom c where (c.host.id = :user1 and c.guest.id = :user2) or (c.host.id =:user2 and c.guest.id=:user1)")
    Optional<ChatRoom> findChatRoomByUsers(@Param("user1") Long user1, @Param("user2") Long user2);
}

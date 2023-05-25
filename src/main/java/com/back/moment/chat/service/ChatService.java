package com.back.moment.chat.service;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatResponseDto;
import com.back.moment.chat.dto.ChatRoomResponseDto;
import com.back.moment.chat.entity.ChatRoom;
import com.back.moment.chat.repository.ChatRepository;
import com.back.moment.chat.repository.ChatRoomRepository;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final UsersRepository userRepository;

    /*
    이미 사용자 둘간의 채팅방이 존재한다면,
    기존에 있던 채팅방의 id를 넘겨준다.
    그렇지 않다면 , 방을 새로 생성해서 반환
     */
    public ChatRoomResponseDto createChatRoom(Long sender , Long receriver){
        userRepository.findById(receriver).orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자"));
        Optional<ChatRoom> chatRoomByUsers = chatRoomRepository.findChatRoomByUsers(sender, receriver);
        if(chatRoomByUsers.isPresent()){
            return new ChatRoomResponseDto(chatRoomByUsers.get().getId());
        }
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.of(sender, receriver));
        return new ChatRoomResponseDto(chatRoom.getId());
    }
    /*
    방에 입장한다. 해당 방 Id에 대한
    채팅이력이 있다면 , 페이징처리로 반환해준다.
     */
    public List<ChatResponseDto> enterChatRoom(Long roomId, Pageable pageable){
        return chatRepository.findByRoomIdOrderByCreatedAt(roomId,pageable).getContent().stream().map(c-> ChatResponseDto.from(c)).collect(Collectors.toList());
    }
    /*
    채팅 내용을 저장한다.
    채팅은 MongoDB에 저장한다.
     */
    public void saveChat(ChatRequestDto chatDto){
        chatRepository.save(ChatRequestDto.toEntity(chatDto, LocalDateTime.now()));
    }
}

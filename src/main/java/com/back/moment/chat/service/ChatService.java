package com.back.moment.chat.service;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatResponseDto;
import com.back.moment.chat.dto.ChatRoomInfoResponseDto;
import com.back.moment.chat.dto.ChatRoomResponseDto;
import com.back.moment.chat.entity.Chat;
import com.back.moment.chat.entity.ChatRoom;
import com.back.moment.chat.repository.ChatRepository;
import com.back.moment.chat.repository.ChatRoomRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final UsersRepository userRepository;
    private final MongoTemplate mongoTemplate;
    /*
    방에 입장하는 메서드. 우선 채팅을 했던 내역이 있으면,
    내역들을 보내주어야 한다. 그리고 읽지않음을 모두 읽음으로 변경한다.
    채팅은 생성된 순으로 먼저 보낸다. 방이 없다면 , chatRoomId , chatList를
    모두 우선적으로 null로 반환한다. (좋은 방법인지는 모르겠음)
     */
    public ResponseEntity<ChatRoomResponseDto> enterChatRoom(Users userOne , Long userTwoId) {
        Users userTwo = userRepository.findById(userTwoId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByUsers(userOne.getId(), userTwoId);
        ChatRoomResponseDto chatRoomResponseDto;
        if (chatRoom.isPresent()) {
            ChatRoom findChatRoom = chatRoom.get();
            List<Chat> chatList;
            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("chatRoomId").is(findChatRoom.getId()),
                    Criteria.where("senderId").is(userTwo.getId()),
                    Criteria.where("readStatus").is(false)
            ));
            Update update = Update.update("readStatus", true);
            mongoTemplate.updateMulti(query, update, Chat.class);
            if (findChatRoom.getGuest().equals(userOne.getId())) {
                chatList = chatRepository.findByChatRoomIdAndCreatedAtAfter(findChatRoom.getId(),findChatRoom.getGuestEntryTime());
            } else {
                chatList = chatRepository.findByChatRoomIdAndCreatedAtAfter(findChatRoom.getId(), findChatRoom.getHostEntryTime());
            }
            List<ChatResponseDto> chatListDto = chatList.stream().map(ChatResponseDto::from).collect(Collectors.toList());
            chatRoomResponseDto = new ChatRoomResponseDto(findChatRoom.getId(), chatListDto, userTwo.getProfileImg(), userTwoId, userTwo.getNickName());
            return ResponseEntity.ok(chatRoomResponseDto);
        } else {
            chatRoomResponseDto = new ChatRoomResponseDto(null, null, userTwo.getProfileImg(), userTwoId, userTwo.getNickName());
            return ResponseEntity.ok(chatRoomResponseDto);
        }
    }
    /*
    방을 생성하는 로직이다.
    생성하고  chatRoom의 Id를 반환한다.
     */
    public Long createChatRoom(ChatRequestDto chatRequestDto){
        Users userOne = userRepository.findById(chatRequestDto.getSenderId()).orElseThrow(()-> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        Users userTwo = userRepository.findById(chatRequestDto.getReceiverId()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        ChatRoom chatRoom = ChatRoom.of(userOne, userTwo,LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }
    /*
    채팅 내용을 저장한다.
    채팅은 MongoDB에 저장한다.
     */
    public ChatResponseDto saveChat(ChatRequestDto chatRequestDto){
        chatRoomRepository.findById(chatRequestDto.getChatRoomId()).orElseThrow(() -> new ApiException(ExceptionEnum.RUNTIME_EXCEPTION));
        Chat chat = chatRepository.save(ChatRequestDto.toEntity(chatRequestDto, LocalDateTime.now()));
        return ChatResponseDto.from(chat);
    }
    /*
    모든 채팅방을 모두 가져온다.
    이 로직에서 MessageAt이 가장 최근인
    채팅방부터 가져오고 , 현재 로그인된 사용자가 userOne에 존재하는지,
    userTwo에 존재하는지도 확인해서 , 상대 사용자의 정보도 같이 보내줌.
     */
    public ResponseEntity<Queue<ChatRoomInfoResponseDto>> findAllChatRoom(Users user){
        Queue<ChatRoomInfoResponseDto> chatRoomInfoResponseDtoQueue = new PriorityQueue<>(new Comparator<ChatRoomInfoResponseDto>() {
            @Override
            public int compare(ChatRoomInfoResponseDto o1, ChatRoomInfoResponseDto o2) {
                return o2.getLastChatTime().compareTo(o1.getLastChatTime());
            }
        });
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByHostOrGuest(user, user);
        for (ChatRoom chatRoom : chatRoomList) {
            Chat chat;
            ChatRoomInfoResponseDto chatRoomInfoResponseDto;
            if(chatRoom.getHost().getId().equals(user.getId())){
                Optional<Chat> chatOptional = chatRepository.findTopByChatRoomIdAndCreatedAtAfterOrderByCreatedAtDesc(chatRoom.getId(), chatRoom.getHostEntryTime());
                System.out.println(chatRoom.getHostEntryTime());
                if(chatOptional.isPresent()) {
                    chat = chatOptional.get();
                    chatRoomInfoResponseDto = new ChatRoomInfoResponseDto(chatRoom.getId(),chat.getCreatedAt(),ChatResponseDto.from(chat),chatRoom.getGuest().getProfileImg(),chatRoom.getGuest().getId(),chatRoom.getGuest().getNickName());
                } else{
                    chat = null;
                    chatRoomInfoResponseDto = null;
                }
            }
            else{
                Optional<Chat> chatOptional = chatRepository.findTopByChatRoomIdAndCreatedAtAfterOrderByCreatedAtDesc(chatRoom.getId(), chatRoom.getGuestEntryTime());
                if(chatOptional.isPresent()) {
                    chat = chatOptional.get();
                    chatRoomInfoResponseDto = new ChatRoomInfoResponseDto(chatRoom.getId(),chat.getCreatedAt(),ChatResponseDto.from(chat),chatRoom.getHost().getProfileImg(),chatRoom.getHost().getId(),chatRoom.getHost().getNickName());
                } else{
                    chat = null;
                    chatRoomInfoResponseDto = null;
                }
            }
            if(chatRoomInfoResponseDto!=null) chatRoomInfoResponseDtoQueue.add(chatRoomInfoResponseDto);
        }
        return ResponseEntity.ok(chatRoomInfoResponseDtoQueue);
    }
    /*
    유저가 채팅방을 삭제하면 , 삭제를 하지않고,
    해당 유저에게 채팅방의 채팅이 보이지 않게함.

     */
    public ResponseEntity<String> deleteChatRoom(Users user,Long chatRoomId){
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_CHATROOM));
        if(findChatRoom.getHost().getId().equals(user.getId())){
            findChatRoom.updateHostEntryTime(LocalDateTime.now());
        }else{
            findChatRoom.updateGuestEntryTime(LocalDateTime.now());
        }
        return ResponseEntity.ok("success");
    }
}

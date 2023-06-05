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
import com.back.moment.global.service.RedisService;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
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
    private final RedisService redisService;
    /*
    방에 입장하는 메서드. 우선 채팅을 했던 내역이 있으면,
    내역들을 보내주어야 한다. 그리고 읽지않음을 모두 읽음으로 변경한다.
    Redis에 채팅이 있으면 해당 채팅도 전부 같이 보여준다.
     */
    public ResponseEntity<ChatRoomResponseDto> enterChatRoom(Users userOne , Long userTwoId) {
        Users userTwo = userRepository.findById(userTwoId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByUsers(userOne.getId(), userTwoId);
        ChatRoomResponseDto chatRoomResponseDto;
        if (chatRoom.isPresent()) {
            ChatRoom findChatRoom = chatRoom.get();
            redisService.saveChatsToDB(findChatRoom.getId());
            List<Chat> chatList;
            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("chatRoomId").is(findChatRoom.getId()),
                    Criteria.where("senderId").is(userTwo.getId()),
                    Criteria.where("readStatus").is(false)
            ));
            Update update = Update.update("readStatus", true);
            mongoTemplate.updateMulti(query, update, Chat.class);
            if (findChatRoom.getGuest().getId().equals(userOne.getId())) {
                chatList = chatRepository.findByChatRoomIdAndCreatedAtAfter(findChatRoom.getId(),findChatRoom.getGuestEntryTime());
            } else {
                chatList = chatRepository.findByChatRoomIdAndCreatedAtAfter(findChatRoom.getId(), findChatRoom.getHostEntryTime());
            }
            List<ChatResponseDto> chatListDto = chatList.stream().map(ChatResponseDto::from).collect(Collectors.toList());
            chatListDto.sort(new Comparator<ChatResponseDto>() {
                @Override
                public int compare(ChatResponseDto o1, ChatResponseDto o2) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
            });
            chatRoomResponseDto = new ChatRoomResponseDto(findChatRoom.getId(),
                                                          chatListDto,
                                                          userTwo.getProfileImg(),
                                                          userTwoId,
                                                          userTwo.getNickName());
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
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return savedChatRoom.getId();
    }
    /*
    채팅 내용을 저장한다.
    채팅은 MongoDB에 저장한다.
     */
    public ChatResponseDto saveChat(ChatRequestDto chatRequestDto){
        Chat chat = ChatRequestDto.toEntity(chatRequestDto,LocalDateTime.now());
        redisService.setChatValues(chat, chat.getChatRoomId(),chat.getUuid());
        if(redisService.getChat(chat.getChatRoomId(),chat.getUuid())==null) throw new ApiException(ExceptionEnum.FAIL_CHAT_SAVE);
        return ChatResponseDto.from(chat);
    }
    /*
    모든 채팅방을 가져온다.
    이전에 Redis에 남아있는 채팅들을 전부 DB에 insert 처리
    해당 채팅방에 읽지않은 메시지가 있었는지 없었는지 보여준다.
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
            redisService.saveChatsToDB(chatRoom.getId());
            Chat chat;
            ChatRoomInfoResponseDto chatRoomInfoResponseDto;
            if(chatRoom.getHost().getId().equals(user.getId())){
                Optional<Chat> chatOptional = chatRepository.findTopByChatRoomIdAndCreatedAtAfterOrderByCreatedAtDesc(chatRoom.getId(), chatRoom.getHostEntryTime());
                if(chatOptional.isPresent()) {
                    chat = chatOptional.get();
                    if(chat.getReceiverId().equals(user.getId()) && chat.getReadStatus().equals(false)){
                        chatRoomInfoResponseDto = new ChatRoomInfoResponseDto(chatRoom.getId(),
                                                                              chat.getCreatedAt(),
                                                                              ChatResponseDto.from(chat),
                                                                              chatRoom.getGuest().getProfileImg(),
                                                                              chatRoom.getGuest().getId(),
                                                                              chatRoom.getGuest().getNickName(),true);
                    }else{
                        chatRoomInfoResponseDto = new ChatRoomInfoResponseDto(chatRoom.getId(),
                                                                              chat.getCreatedAt(),
                                                                              ChatResponseDto.from(chat),
                                                                              chatRoom.getGuest().getProfileImg(),
                                                                              chatRoom.getGuest().getId(),
                                                                              chatRoom.getGuest().getNickName(),false);
                    }

                } else{
                    chat = null;
                    chatRoomInfoResponseDto = null;
                }
            }
            else{
                Optional<Chat> chatOptional = chatRepository.findTopByChatRoomIdAndCreatedAtAfterOrderByCreatedAtDesc(chatRoom.getId(), chatRoom.getGuestEntryTime());
                if(chatOptional.isPresent()) {
                    chat = chatOptional.get();
                    if(chat.getReceiverId().equals(user.getId()) && chat.getReadStatus().equals(false)){
                        chatRoomInfoResponseDto = new ChatRoomInfoResponseDto(chatRoom.getId(),
                                                                              chat.getCreatedAt(),
                                                                              ChatResponseDto.from(chat),
                                                                              chatRoom.getHost().getProfileImg(),
                                                                              chatRoom.getHost().getId(),
                                                                              chatRoom.getHost().getNickName(),true);
                    }else{
                        chatRoomInfoResponseDto = new ChatRoomInfoResponseDto(chatRoom.getId(),
                                                                              chat.getCreatedAt(),
                                                                              ChatResponseDto.from(chat),
                                                                              chatRoom.getHost().getProfileImg(),
                                                                              chatRoom.getHost().getId(),
                                                                              chatRoom.getHost().getNickName(),false);
                    }
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
    읽음 처리를 해주는 메서드
    우선적으로 redis에 저장되어있는 채팅을 update한다.
     */
    public ResponseEntity<String> markAsRead(ChatResponseDto chatResponseDto){
        Chat chat = redisService.getChat(chatResponseDto.getChatRoomId(), chatResponseDto.getUuid());
        chat.updateReadStatus();
        redisService.setChatValues(chat,chat.getChatRoomId(),chat.getUuid());
        return ResponseEntity.ok("success");
    }
    public ResponseEntity<String> saveChatList(Long chatRoomId){
        redisService.saveChatsToDB(chatRoomId);
        return ResponseEntity.ok("success");
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

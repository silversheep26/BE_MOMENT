package com.back.moment.chat.service;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final MongoTemplate mongoTemplate;
    /*
    방에 입장하는 메서드. 우선 채팅을 했던 내역이 있으면,
    내역들을 보내주어야 한다. 그리고 읽지않음을 모두 읽음으로 변경한다.
    채팅은 생성된 순으로 먼저 보낸다. 방이 없다면 , chatRoomId , chatList를
    모두 우선적으로 null로 반환한다. (좋은 방법인지는 모르겠음)
     */
    public ResponseEntity<ChatRoomResponseDto> enterChatRoom(Users userOne , Long userTwoId){
        Long userOneId = userOne.getId();
        Users userTwo = userRepository.findById(userTwoId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        Optional<ChatRoom> chatRoomByUsers = chatRoomRepository.findChatRoomByUsers(userOne, userTwo);
        if(chatRoomByUsers.isPresent()){
            boolean isUserOne=false;
            if(chatRoomByUsers.get().getUserOne().getId().equals(userOneId)) isUserOne = true;
            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("chatRoomId").is(chatRoomByUsers.get().getId()),
                    Criteria.where("senderId").is(userTwo.getId()),
                    Criteria.where("readStatus").is(false)
                    ));
            Update update = Update.update("readStatus", true);
            mongoTemplate.updateMulti(query,update,Chat.class);
            List<ChatResponseDto> chatResponseDtoList;
            if(isUserOne){
                chatResponseDtoList = chatRepository.findByChatRoomIdAndUserOneCanSeeTrueOrderByCreatedAt(chatRoomByUsers.get().getId()).stream().map(c -> ChatResponseDto.from(c)).collect(Collectors.toList());
                return ResponseEntity.ok(new ChatRoomResponseDto(chatRoomByUsers.get().getId(),chatResponseDtoList,userTwo.getProfileImg(),userTwoId,userTwo.getNickName(),false));
            }
            chatResponseDtoList = chatRepository.findByChatRoomIdAndUserTwoCanSeeTrueOrderByCreatedAt(chatRoomByUsers.get().getId()).stream().map(c -> ChatResponseDto.from(c)).collect(Collectors.toList());
            return ResponseEntity.ok(new ChatRoomResponseDto(chatRoomByUsers.get().getId(),chatResponseDtoList,userOne.getProfileImg(),userOneId,userOne.getNickName(),false));
        }
        return ResponseEntity.ok(new ChatRoomResponseDto(null,null,userTwo.getProfileImg(),userTwoId,userTwo.getNickName(),false));
    }
    /*
    방을 생성하는 로직이다.
    생성하고  chatRoom의 Id를 반환한다.
     */
    public Long createChatRoom(ChatRequestDto chatRequestDto){
        Users userOne = userRepository.findById(chatRequestDto.getSenderId()).orElseThrow(()-> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        Users userTwo = userRepository.findById(chatRequestDto.getReceiverId()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER));
        ChatRoom chatRoom = ChatRoom.of(userOne, userTwo);
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }
    /*
    채팅 내용을 저장한다.
    채팅은 MongoDB에 저장한다.
     */
    public ChatResponseDto saveChat(ChatRequestDto chatRequestDto,Long chatRoomId){
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ApiException(ExceptionEnum.RUNTIME_EXCEPTION));
        chatRoom.updateCanUserOneSee();
        chatRoom.updateCanUserTwoSee();
        Chat chat = chatRepository.save(ChatRequestDto.toEntity(chatRequestDto, LocalDateTime.now()));
        chatRoom.updateLastMessageAt(LocalDateTime.now());
        return ChatResponseDto.from(chat);
    }
    /*
    모든 채팅방을 모두 가져온다.
    이 로직에서 MessageAt이 가장 최근인
    채팅방부터 가져오고 , 현재 로그인된 사용자가 userOne에 존재하는지,
    userTwo에 존재하는지도 확인해서 , 상대 사용자의 정보도 같이 보내줌.
     */
    public ResponseEntity<List<ChatRoomResponseDto>> findAllChatRoom(Users user){
        ArrayList<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserOneOrUserTwoOrderByLastMessageAtDesc(user, user);
        for (ChatRoom chatRoom : chatRoomList) {
            boolean existReadStatusIsFalse;
            if(chatRoom.getUserOne().getId().equals(user.getId())){
                Users userTwo = chatRoom.getUserTwo();
                if(chatRoom.getCanUserOneSee()){
                    existReadStatusIsFalse = chatRepository.existsByChatRoomIdAndUserOneCanSeeTrueAndReadStatusFalseAndReceiverId(chatRoom.getId(), user.getId());
                    chatRoomResponseDtoList.add(new ChatRoomResponseDto(chatRoom.getId(),null,userTwo.getProfileImg(),userTwo.getId(),userTwo.getNickName(),existReadStatusIsFalse));
                }
            }else{
                Users userOne = chatRoom.getUserOne();
                if(chatRoom.getCanUserOneSee()){
                    existReadStatusIsFalse = chatRepository.existsByChatRoomIdAndUserTwoCanSeeTrueAndReadStatusFalseAndReceiverId(chatRoom.getId(), user.getId());
                    chatRoomResponseDtoList.add(new ChatRoomResponseDto(chatRoom.getId(),null,userOne.getProfileImg(),userOne.getId(),userOne.getNickName(),existReadStatusIsFalse));
                }
            }
        }
        return ResponseEntity.ok(chatRoomResponseDtoList);
    }
    /*
    유저가 채팅방을 삭제하면 , 삭제를 하지않고,
    해당 유저에게 채팅방을 보이지 않게함.
     */
    public ResponseEntity<String> deleteChatRoom(Users user,Long chatRoomId){
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_CHATROOM));
        if(findChatRoom.getUserOne().getId().equals(user.getId())){
            findChatRoom.updateCanNotUserOneSee();
            deleteChat(chatRoomId,true);
        }else{
            findChatRoom.updateCanNotUserTwoSee();
            deleteChat(chatRoomId,false);
        }
        return ResponseEntity.ok("success");
    }
    /*
    채팅을 보이지 않게 update하는것은 비동기적으로 처리해주고 싶어서
    @Async 어노테이션을 붙이고 deleteChatRoom에서 deleteChat
    메서드를 사용했다.
     */
    @Async
    public void deleteChat(Long chatRoomId,boolean isUserOne){
        List<Chat> chatList = chatRepository.findByChatRoomId(chatRoomId);
        Query query = new Query();
        Update update=null;
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("chatRoomId").is(chatRoomId)
        ));
        if(isUserOne){
             update=Update.update("userOneCanSee", false);
        }else{
            update=Update.update("userTwoCanSee",false);
        }
        mongoTemplate.updateMulti(query,update,Chat.class);
    }


}

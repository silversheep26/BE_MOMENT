package com.back.moment.chat.service;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatResponseDto;
import com.back.moment.chat.dto.ChatRoomResponseDto;
import com.back.moment.chat.entity.Chat;
import com.back.moment.chat.entity.ChatRoom;
import com.back.moment.chat.repository.ChatRepository;
import com.back.moment.chat.repository.ChatRoomRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.global.service.RedisService;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UsersRepository userRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private RedisService redisService;
    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("채팅방 입장 실패 테스트 , 상대 유저가 없는 유저일때")
    public void fail_enter_chatRoom(){
        //given
        Users users = new Users();
        Long userOneIdLong = 1L;
        users.setId(userOneIdLong);
        Long userTwoIdLong = 2L;
        //when
        when(userRepository.findById(userTwoIdLong)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(()->chatService.enterChatRoom(users,userTwoIdLong)).isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("채팅방 입장 성공 테스트, 채팅방이 있는 경우 , 유저1이 Host")
    public void success_enter_chatroom_given_chatRoom_And_userOneIsHost(){
        Users user1 = new Users();
        Users user2 = new Users();
        Long userOneIdLong = 1L;
        user1.setId(userOneIdLong);
        Long userTwoIdLong = 2L;
        user2.setId(userTwoIdLong);
        user2.setNickName("테스트 유저");
        user2.setProfileImg("프로필 이미지 url");
        ChatRoom chatRoom = ChatRoom.of(user1, user2, LocalDateTime.now());
        chatRoom.setId(1L);
        chatRoom.setHost(user1);
        chatRoom.setHostEntryTime(LocalDateTime.now());
        ArrayList<Chat> chatList = new ArrayList<>();
        chatList.add(Chat.of("테스트",1L,2L,1L,LocalDateTime.now()));
        chatList.add(Chat.of("테스트",1L,2L,1L,LocalDateTime.now()));
        when(userRepository.findById(userTwoIdLong)).thenReturn(Optional.of(user2));
        when(chatRoomRepository.findChatRoomByUsers(userOneIdLong,userTwoIdLong)).thenReturn(Optional.of(chatRoom));
        when(chatRepository.findByChatRoomIdAndCreatedAtAfter(chatRoom.getId(),chatRoom.getHostEntryTime()))
                .thenReturn(chatList);
        List<ChatResponseDto> chatResponseDtoList = chatList.stream().map(ChatResponseDto::from).collect(Collectors.toList());
        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto(chatRoom.getId(), chatResponseDtoList, user2.getProfileImg(), userTwoIdLong, user2.getNickName());
        chatResponseDtoList.sort(Comparator.comparing(ChatResponseDto::getCreatedAt).reversed());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getChatRoomId())
                .isEqualTo(chatRoomResponseDto.getChatRoomId());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getChatList().size())
                .isEqualTo(chatRoomResponseDto.getChatList().size());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverId())
                .isEqualTo(2L);
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverNickName())
                .isEqualTo("테스트 유저");
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverProfileImg())
                .isEqualTo("프로필 이미지 url");
    }

    @Test
    @DisplayName("채팅방 입장 성공 테스트, 채팅방이 있는 경우 , 유저1이 Guest")
    public void success_enter_chatroom_given_chatRoom_And_userOneIsGuest(){
        Users user1 = new Users();
        Users user2 = new Users();
        Long userOneIdLong = 1L;
        user1.setId(userOneIdLong);
        Long userTwoIdLong = 2L;
        user2.setId(userTwoIdLong);
        user2.setNickName("테스트 유저");
        user2.setProfileImg("프로필 이미지 url");
        ChatRoom chatRoom = ChatRoom.of(user1, user2, LocalDateTime.now());
        chatRoom.setId(1L);
        chatRoom.setGuest(user1);
        chatRoom.setGuestEntryTime(LocalDateTime.now());
        ArrayList<Chat> chatList = new ArrayList<>();
        chatList.add(Chat.of("테스트",1L,2L,1L,LocalDateTime.now()));
        chatList.add(Chat.of("테스트",1L,2L,1L,LocalDateTime.now()));
        when(userRepository.findById(userTwoIdLong)).thenReturn(Optional.of(user2));
        when(chatRoomRepository.findChatRoomByUsers(userOneIdLong,userTwoIdLong)).thenReturn(Optional.of(chatRoom));
        when(chatRepository.findByChatRoomIdAndCreatedAtAfter(chatRoom.getId(),chatRoom.getGuestEntryTime()))
                .thenReturn(chatList);
        List<ChatResponseDto> chatResponseDtoList = chatList.stream().map(ChatResponseDto::from).collect(Collectors.toList());
        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto(chatRoom.getId(), chatResponseDtoList, user2.getProfileImg(), userTwoIdLong, user2.getNickName());
        chatResponseDtoList.sort(Comparator.comparing(ChatResponseDto::getCreatedAt).reversed());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getChatRoomId())
                .isEqualTo(chatRoomResponseDto.getChatRoomId());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getChatList().size())
                .isEqualTo(chatRoomResponseDto.getChatList().size());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverId())
                .isEqualTo(2L);
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverNickName())
                .isEqualTo("테스트 유저");
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverProfileImg())
                .isEqualTo("프로필 이미지 url");
    }
    @Test
    @DisplayName("채팅방 입장 성공 테스트, 채팅방이 없는 경우")
    public void success_enter_chatroom_given_No_chatRoom(){
        Users user1 = new Users();
        Users user2 = new Users();
        Long userOneIdLong = 1L;
        user1.setId(userOneIdLong);
        Long userTwoIdLong = 2L;
        user2.setId(userTwoIdLong);
        user2.setNickName("테스트 유저");
        user2.setProfileImg("프로필 이미지 url");
        when(userRepository.findById(userTwoIdLong)).thenReturn(Optional.of(user2));
        when(chatRoomRepository.findChatRoomByUsers(userOneIdLong,userTwoIdLong)).thenReturn(Optional.empty());
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getChatRoomId())
                .isEqualTo(null);
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getChatList())
                .isEqualTo(null);
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverId())
                .isEqualTo(2L);
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverNickName())
                .isEqualTo("테스트 유저");
        assertThat(chatService.enterChatRoom(user1,userTwoIdLong).getBody().getReceiverProfileImg())
                .isEqualTo("프로필 이미지 url");
    }
    @Test
    @DisplayName("채팅방 생성 실패 테스트, sender 존재하지 않는 경우")
    public void fail_create_chatRoom_given_ChatRequestDto_Sender_NotExist(){
        Long senderId = 1L;
        Long receiverId =2L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setChatRoomId(null);
        chatRequestDto.setMessage("방 하나 만들어주세요");
        chatRequestDto.setSenderId(senderId);
        chatRequestDto.setReceiverId(receiverId);
        when(userRepository.findById(senderId)).thenReturn(Optional.empty());
        assertThatThrownBy(()->chatService.createChatRoom(chatRequestDto)).isInstanceOf(ApiException.class);
    }
    @Test
    @DisplayName("채팅방 생성 실패 테스트, receiver 존재하지 않는 경우")
    public void fail_create_chatRoom_given_ChatRequestDto_Receiver_NotExist(){
        Users users = new Users();
        Long senderId = 1L;
        users.setId(senderId);
        Long receiverId =2L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setChatRoomId(null);
        chatRequestDto.setMessage("방 하나 만들어주세요");
        chatRequestDto.setSenderId(senderId);
        chatRequestDto.setReceiverId(receiverId);
        when(userRepository.findById(senderId)).thenReturn(Optional.of(users));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());
        assertThatThrownBy(()->chatService.createChatRoom(chatRequestDto)).isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("채팅방 생성 성공 테스트")
    public void success_create_chatRoom_given_ChatRequestDto_then_Return_ChatRoomId(){
        Users userOne = new Users();
        Users userTwo = new Users();
        Long senderId = 1L;
        userOne.setId(senderId);
        Long receiverId =2L;
        userTwo.setId(receiverId);
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setChatRoomId(null);
        chatRequestDto.setMessage("방 하나 만들어주세요");
        chatRequestDto.setSenderId(senderId);
        chatRequestDto.setReceiverId(receiverId);
        when(userRepository.findById(senderId)).thenReturn(Optional.of(userOne));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(userTwo));
        LocalDateTime now = LocalDateTime.now();
        ChatRoom chatRoom = ChatRoom.of(userOne, userTwo, now);
        chatRoom.setId(1L);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);
        assertThat(chatService.createChatRoom(chatRequestDto)).isEqualTo(chatRoom.getId());
    }

    @Test
    @DisplayName("채팅 저장 실패 테스트")
    public void fail_save_Chat_given_ChatRequestDto(){
        Users userOne = new Users();
        Users userTwo = new Users();
        Long senderId = 1L;
        userOne.setId(senderId);
        Long receiverId =2L;
        userTwo.setId(receiverId);
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setChatRoomId(1L);
        chatRequestDto.setMessage("안녕하세요?");
        chatRequestDto.setSenderId(senderId);
        chatRequestDto.setReceiverId(receiverId);
        Chat chat = ChatRequestDto.toEntity(chatRequestDto, LocalDateTime.now());
        redisService.setChatValues(chat, chat.getChatRoomId(),chat.getUuid());
        when(redisService.getChat(any(Long.class),any(String.class))).thenReturn(null);
        assertThatThrownBy(()->chatService.saveChat(chatRequestDto)).isInstanceOf(ApiException.class);
    }
    @Test
    @DisplayName("채팅 저장 성공 테스트")
    public void fail_save_Chat_given_ChatRequestDto_then_Return_ChatResponseDto(){
        Users userOne = new Users();
        Users userTwo = new Users();
        Long senderId = 1L;
        userOne.setId(senderId);
        Long receiverId =2L;
        userTwo.setId(receiverId);
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setChatRoomId(1L);
        chatRequestDto.setMessage("안녕하세요?");
        chatRequestDto.setSenderId(senderId);
        chatRequestDto.setReceiverId(receiverId);
        Chat chat = ChatRequestDto.toEntity(chatRequestDto, LocalDateTime.now());
        when(redisService.getChat(anyLong(),anyString())).thenReturn(chat);
        assertThat(chatService.saveChat(chatRequestDto).getChatRoomId()).isEqualTo(1L);
    }
}
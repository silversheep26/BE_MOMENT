package com.back.moment.chat.controller;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatRoomResponseDto;
import com.back.moment.chat.service.ChatService;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatRoomService;
    private final SimpMessagingTemplate msgOperation;

    /*
    채팅방에 입장 , DM 을 보내는 화면에 들어갔을때 요청을 보내면
    그동안의 채팅내역들이 보여지고 , 읽지않았던 채팅들이 있으면 읽음으로 상태를 바꾼다.
     */
    @GetMapping("chatRoom/enter/{userId}")
    public ResponseEntity<ChatRoomResponseDto> enterChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId){
        return chatRoomService.enterChatRoom(userDetails.getUsers(),userId);
    }
    /*
    내가 소속되어 있고 , 내가 채팅방을 삭제하지 않은 채팅방 목록들을 가져온다.
    DM 목록을 볼때 호출하는 컨트롤러
     */
    @GetMapping("chatRoom/list")
    public ResponseEntity<List<ChatRoomResponseDto>> findAllChatRoomByUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.findAllChatRoom(userDetails.getUsers());
    }
    /*
    웹소켓을 통해 채팅을 시작하고 , 첫 채팅을 보내면
    채팅방을 생성한다. 우선 채팅방이 없는 상태이면
    /sub/chat/room으로 먼저 채팅방 Id 를 보낸다.
     */
    @MessageMapping("/chat/send")
    @SendTo("/sub/chat/room")
    public void enterChatRoom(ChatRequestDto chatRequestDto){
        Long chatRoomId=null;
        if(chatRequestDto.getChatRoomId()==null){
            chatRoomId = chatRoomService.createChatRoom(chatRequestDto);
            msgOperation.convertAndSend("/sub/chat/room",chatRoomId);
        }
        chatRequestDto.setChatRoomId(chatRoomId);
        chatRoomService.saveChat(chatRequestDto,chatRoomId);
        msgOperation.convertAndSend("/sub/chat/room/"+chatRequestDto.getChatRoomId(),chatRequestDto);
    }
}

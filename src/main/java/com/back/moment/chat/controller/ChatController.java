package com.back.moment.chat.controller;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatResponseDto;
import com.back.moment.chat.dto.ChatRoomResponseDto;
import com.back.moment.chat.service.ChatService;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatRoomService;
    private final SimpMessagingTemplate msgOperation;

    /*
    Dto 반환에 대한 리팩토링 필요 ,
    개인 메시지를 위한 방 Id 반환
     */
    @PostMapping("/create/{userId}")
    public ChatRoomResponseDto createChat(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId){
        return chatRoomService.createChatRoom(userDetails.getUsers().getId(),userId);
    }
    /*
    1:1 채팅방에 입장,
    대화 내용을 페이징처리로 반환
     */
    @GetMapping("/enter/{roomId}")
    public List<ChatResponseDto> enterChatRoom(@PathVariable Long roomId, @RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return chatRoomService.enterChatRoom(roomId,pageable);
    }
    /*
    웹소켓을 사용해서 채팅을 보낸다,
    구독자들에게 채팅이 보내짐.
     */
    @MessageMapping("/chat/send")
    public void chat(ChatRequestDto chatDto){
        chatRoomService.saveChat(chatDto);
        msgOperation.convertAndSend("/sub/chat/"+chatDto.getRoomId(),chatDto);
    }
}

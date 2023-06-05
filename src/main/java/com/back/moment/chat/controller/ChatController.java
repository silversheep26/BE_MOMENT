package com.back.moment.chat.controller;

import com.back.moment.chat.dto.ChatRequestDto;
import com.back.moment.chat.dto.ChatResponseDto;
import com.back.moment.chat.dto.ChatRoomInfoResponseDto;
import com.back.moment.chat.dto.ChatRoomResponseDto;
import com.back.moment.chat.service.ChatService;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Queue;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate msgOperation;

    /*
    채팅방에 입장 , DM 을 보내는 화면에 들어갔을때 요청을 보내면
    그동안의 채팅내역들이 보여지고 , 읽지않았던 채팅들이 있으면 읽음으로 상태를 바꾼다.
     */
    @GetMapping("chatRoom/enter/{userId}")
    public ResponseEntity<ChatRoomResponseDto> enterChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId){
        return chatService.enterChatRoom(userDetails.getUsers(),userId);
    }
    /*
    내가 소속되어 있고 , 내가 채팅방을 삭제하지 않은 채팅방 목록들을 가져온다.
    DM 목록을 볼때 호출하는 컨트롤러
     */
    @GetMapping("chatRoom/list")
    public ResponseEntity<Queue<ChatRoomInfoResponseDto>> findAllChatRoomByUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatService.findAllChatRoom(userDetails.getUsers());
    }
    /*
    채팅방 삭제하는 컨트롤러, 사실 삭제를 하지않는다.
    서비스 단에서 삭제를 요청한 유저에게만 채팅방이
    보이지 않게끔 하는 컨트롤러
     */
    @DeleteMapping("chatRoom/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable Long chatRoomId){
        return chatService.deleteChatRoom(userDetails.getUsers(),chatRoomId);
    }
    /*
    웹소켓을 통해 채팅을 시작하고 , 첫 채팅을 보내면
    채팅방을 생성한다. 우선 채팅방이 없는 상태이면
    /sub/chat/room으로 먼저 채팅방 Id 를 보낸다.
    그다음 /sub/chat/room/{chatRoomId} 에 chatRequestDto를 보내고,
    그다음 /sub/chat{receiverId}에 알림을 보냄 , 이쪽으로 값을 따로 보내는건 없다.
     */
    @MessageMapping("/chat/send")
    public void enterChatRoom(ChatRequestDto chatRequestDto){
        if(chatRequestDto.getChatRoomId()==null){
            Long chatRoomId = chatService.createChatRoom(chatRequestDto); // 첫 채팅이면 , 채팅방을 우선 만들고 채팅방의 Id값을 반환한다.
            chatRequestDto.setChatRoomId(chatRoomId); // Dto에 채팅Id를 넣어준다.
            msgOperation.convertAndSend("/sub/chat/room",chatRoomId); // 우선적으로 채팅방의 Id 메시지를 한번 보낸다. 프론트에서 채팅방의 아이디를 받아서 사용하게끔
        }
        ChatResponseDto chatResponseDto = chatService.saveChat(chatRequestDto);
        msgOperation.convertAndSend("/sub/chat/room/"+chatRequestDto.getChatRoomId(),chatResponseDto);
        msgOperation.convertAndSend("/sub/alarm/"+chatRequestDto.getReceiverId(),chatResponseDto); // 알림 기능
    }
    /*
    채팅에 읽었다는 update를 해주기 위함.
    프론트에서 /sub/chat/room + chatReqeustDto.getChatRoomId에 들어가있었으면 ,
    /pub/chat/read로 해당 채팅ResponseDto를 그대로 다시 읽음설정을 해주게끔 한다.
     */
    @MessageMapping("/chat/read")
    public void readChat(ChatResponseDto chatResponseDto){
        chatService.markAsRead(chatResponseDto);
    }
    /*
    채팅방을 나가거나 할 때 , 해당 Destination으로
    채팅방 아이디를 보내서 , Redis에 남아있는 채팅내역들을
    DB에 저장하게끔 한다.
     */
    @MessageMapping("/chat/save/{chatRoomId}")
    public void saveChatList(@DestinationVariable("chatRoomId") Long chatRoomId){
        chatService.saveChatList(chatRoomId);
    }

    //PostMan 연습 컨트롤러
    @PostMapping("/chat/send")
    public ResponseEntity<ChatResponseDto> saveChat(@RequestBody ChatRequestDto chatRequestDto){
        if(chatRequestDto.getChatRoomId()==null){
            Long chatRoomId = chatService.createChatRoom(chatRequestDto);
            chatRequestDto.setChatRoomId(chatRoomId);
        }
        return ResponseEntity.ok(chatService.saveChat(chatRequestDto));
    }
    //PostMan 연습 컨트롤러
    @PostMapping("/chat/read")
    public void readChatTwo(ChatResponseDto chatResponseDto){
        chatService.markAsRead(chatResponseDto);
    }
}

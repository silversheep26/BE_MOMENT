package com.back.moment.sse;

import com.back.moment.chat.dto.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {
    private Map<Long,SseEmitter> sseEmitterMap = new HashMap<>();
    @GetMapping(value = "/chat/alarm/{userId}",produces = "text/event-stream")
    public SseEmitter chatAlarm(@PathVariable Long userId){
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitterMap.put(userId,sseEmitter);
        return sseEmitter;
    }


    public void sendNotification(Long userId, ChatResponseDto chatResponseDto){
        SseEmitter sseEmitter = sseEmitterMap.get(userId);
        if(sseEmitter!=null){
            try {
                sseEmitter.send(SseEmitter.event()
                        .data(chatResponseDto)
                        .name("chatAlarm-event"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

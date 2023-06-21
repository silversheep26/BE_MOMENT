package com.back.moment.sse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @CrossOrigin(origins = "https://www.momentapp.site",methods = {RequestMethod.GET,RequestMethod.OPTIONS})
    @GetMapping(value = "/chat/alarm/{userId}",produces = "text/event-stream")
    public SseEmitter chatAlarm(@PathVariable Long userId, HttpServletResponse response){
        response.setHeader("Connection", "keep-alive");
        return notificationService.subscribe(userId);
    }
}

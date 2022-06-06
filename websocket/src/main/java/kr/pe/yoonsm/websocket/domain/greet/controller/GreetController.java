package kr.pe.yoonsm.websocket.domain.greet.controller;

import kr.pe.yoonsm.websocket.domain.greet.vo.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Slf4j
@Controller
public class GreetController {

    @MessageMapping("/register")
    @SendTo("/topic/public")
    public Message register(
            @Payload Message msg,
            SimpMessageHeaderAccessor headerAccessor) {
            headerAccessor.getSessionAttributes()
                    .put("username",msg.getSender());
            return msg;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/public")
    public Message sendMessage( Message msg){
        log.info("sender : {}",msg.getSender());
        return msg;
    }


}

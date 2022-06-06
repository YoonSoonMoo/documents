package kr.pe.yoonsm.websocket.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {

    public Message<?> preSend(Message<?> message, MessageChannel channel){

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        if(command == null) return message;

        switch (command){

            case CONNECT:
                log.debug("Connect !!");
                break;
            case CONNECTED:
                log.debug("Connected!!");
                break;
            case SUBSCRIBE:
                String subscribe = message.getHeaders().get("simpDestination").toString();
                log.debug("subscribe : {} ",subscribe);
                break;
            case DISCONNECT:
                log.debug("disconnected !!");
                break;
            case SEND:
                break;
            default:
                log.warn("wrong command : {}" , command);
        }

        return message;
    }

}

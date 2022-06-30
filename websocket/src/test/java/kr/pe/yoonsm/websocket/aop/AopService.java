package kr.pe.yoonsm.websocket.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by yoonsm@daou.co.kr on 2022-06-30
 */
@Service
@Slf4j
public class AopService {

    private static int seq = 0;

    @Retry(3)
    public String doSomething(){
        log.info("do something");
        seq++;
        if(seq % 5 == 0){
            throw new IllegalStateException("만든 에러");
        }
        return "ok";
    }
}

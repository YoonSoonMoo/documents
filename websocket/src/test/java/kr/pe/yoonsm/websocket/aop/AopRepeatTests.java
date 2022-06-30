package kr.pe.yoonsm.websocket.aop;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Created by yoonsm@daou.co.kr on 2022-06-29
 */
@SpringBootTest
@Slf4j
@Import(RetryAspect.class)
public class AopRepeatTests {

    @Autowired
    AopService aopService;

    @DisplayName("AOP 테스트")
    @Test
    void test(){
        for(int i=0;i<5;i++){
            aopService.doSomething();
        }
    }

}

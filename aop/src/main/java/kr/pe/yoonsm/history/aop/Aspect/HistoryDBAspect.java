package kr.pe.yoonsm.history.aop.Aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pe.yoonsm.history.aop.common.CommonService;
import kr.pe.yoonsm.history.aop.repository.HistoryDataJpaRepository;
import kr.pe.yoonsm.history.aop.repository.UserDataJpaRepository;
import kr.pe.yoonsm.history.aop.repository.dao.HistoryEntity;
import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import kr.pe.yoonsm.history.aop.repository.dao.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * --> 애스펙트(Aspect) 기본 모듈
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class HistoryDBAspect {

    final HistoryDataJpaRepository historyRepository;
    final UserDataJpaRepository userRepository;

    final CommonService commonService;
    final ObjectMapper objectMapper;

    @Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.UserHistoryDBAnnotation)") // --> 포인트컷(PointCut) 애노테이션 지정
    public Object addHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable { // --> 어드바이스(Advice)

        // Request 에서 전달받은 원본값
        UserDao userDao = (UserDao) Arrays.stream(proceedingJoinPoint.getArgs())
                .sequential()
                .filter(data -> (data instanceof UserDao)).findFirst().orElse(null);
        UserDao compareDbData = objectMapper.convertValue(userRepository.findByUserId(userDao.getUserId()),UserDao.class);

        // Main 처리  JoinPoint 기준 위 / 아래 양쪽에 구현되어 있다.
        Object ret = proceedingJoinPoint.proceed();

        log.info("main process complete!!");
        // insert / update 가 성공일 경우 history를 저장한다.
        if (ret instanceof Boolean && userDao != null) {
            if (((Boolean) ret).booleanValue()) {
                HistoryEntity historyDao = new HistoryEntity();
                historyDao.setLocalDateTime(LocalDateTime.now());
                log.info("UserDao Parameter values {}", userDao);
                // DB에 데이타가 존재하므로 update 처리
                if (compareDbData != null) {
                    String changedString = commonService.diff(userDao, compareDbData, UserDao.class);
                    if (changedString.length() > 0) {
                        historyDao.setChangeData(changedString);
                        historyRepository.save(historyDao);
                    }
                } else {
                    historyDao.setChangeData(userDao.getUserId() + " 신규추가");
                    historyRepository.save(historyDao);
                }
                log.info("History Annotation Changed data : {}", historyDao.getChangeData());
            }
        }
        return ret;
    }


    @Around("within(kr.pe.yoonsm.history.aop.services.*)") // --> 포인트컷(PointCut) 클래스 위치로 선정
    //@Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.TimerAnnotation)")
    public Object addTimer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();
        Object ret = proceedingJoinPoint.proceed();
        watch.stop();
        log.info(">> process time : {} ms", watch.getTotalTimeMillis());
        return ret;
    }
}

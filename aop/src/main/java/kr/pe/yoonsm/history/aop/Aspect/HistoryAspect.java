package kr.pe.yoonsm.history.aop.Aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pe.yoonsm.history.aop.common.CommonService;
import kr.pe.yoonsm.history.aop.repository.HistoryRepository;
import kr.pe.yoonsm.history.aop.repository.UserRepository;
import kr.pe.yoonsm.history.aop.repository.dao.HistoryDao;
import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
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
public class HistoryAspect {

    final HistoryRepository historyRepository;
    final UserRepository userRepository;

    final CommonService commonService;
    final ObjectMapper objectMapper;


    @Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.UserHistoryAnnotation)") // --> 포인트컷(PointCut) 애노테이션 지정
    public Object addHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable { // --> 어드바이스(Advice)

        // Request 에서 전달받은 원본값
        UserDao userDao = (UserDao) Arrays.stream(proceedingJoinPoint.getArgs())
                .sequential()
                .filter(data -> (data instanceof UserDao)).findFirst().orElse(null);
        UserDao userDaoDb = null;

        // 커밋되기 전의 값을 미리 세팅 해야 한다. ( deep copy ) Memory repository 이기 때문에...
        //UserDao userDaoDb = userRepository.findByUserId(userDao.getUserId());
        if (userDao != null) {
            userDaoDb = objectMapper.treeToValue(objectMapper.valueToTree(userRepository.findByUserId(userDao.getUserId())), UserDao.class);
            //BeanUtils.copyProperties(userRepository.findByUserId(userDao.getUserId()), userDaoDb); //-- 내부의 객체가 모두 null 이 아니어야 한다. ( 귀찮음 )
            log.info("UserDao DB values {}", userDaoDb);
        }

        // Main 처리  JoinPoint 기준 위 / 아래 양쪽에 구현되어 있다.
        Object ret = proceedingJoinPoint.proceed();

        log.info("main process complete!!");

        // insert / update 가 성공일 경우 history를 저장한다.
        if (ret instanceof Boolean && userDao != null) {
            if (((Boolean) ret).booleanValue()) {
                HistoryDao historyDao = new HistoryDao();
                historyDao.setSeq(historyRepository.getAllData().size());
                historyDao.setLocalDateTime(LocalDateTime.now());
                log.info("UserDao Parameter values {}", userDao);
                // DB에 데이타가 존재하므로 update 처리
                if (userDaoDb != null) {
                    String changedString = commonService.diff(userDaoDb, userDao, UserDao.class);
                    if (changedString.length() > 0) {
                        historyDao.setChangeData(changedString);
                        historyRepository.addHistory(historyDao);
                    }
                } else {
                    historyDao.setChangeData(userDao.getUserId() + " 신규추가");
                    historyRepository.addHistory(historyDao);
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

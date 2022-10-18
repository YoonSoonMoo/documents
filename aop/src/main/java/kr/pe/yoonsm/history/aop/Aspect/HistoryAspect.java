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

import java.time.LocalDateTime;
import java.util.Arrays;

/**
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

    @Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.UserHistoryAnnotation)")
    public Object addHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // Request 에서 전달받은 원본값
        UserDao userDao = (UserDao) Arrays.stream(proceedingJoinPoint.getArgs())
                .sequential()
                .filter(data -> (data instanceof UserDao)).findFirst().orElse(null);
        UserDao userDaoDb = null;

        // 커밋되기 전의 값을 미리 세팅 해야 한다. ( deep copy ) Memory repository 이기 때문에...
        //UserDao userDaoDb = userRepository.findByUserId(userDao.getUserId());
        if(userDao != null) {
            userDaoDb = objectMapper.treeToValue(objectMapper.valueToTree(userRepository.findByUserId(userDao.getUserId())), UserDao.class);
            log.info("Parameter first Db values {}", userDaoDb);
        }

        // Main 처리
        Object ret = proceedingJoinPoint.proceed();

        log.info("main process complete!!");

        // insert / update 가 성공일 경우 history를 저장한다.
        if (ret instanceof Boolean && userDao != null) {
            if (userDao != null && ((Boolean) ret).booleanValue()) {
                HistoryDao historyDao = new HistoryDao();
                log.info("Parameter values {}", userDao);
                if (userDaoDb != null) {
                    String changedString = commonService.diff(userDaoDb, userDao, UserDao.class);
                    if (changedString.length() > 0) {
                        historyDao.setSeq(historyRepository.getAllData().size());
                        historyDao.setChangeData(changedString);
                        historyDao.setLocalDateTime(LocalDateTime.now());
                        historyRepository.addHistory(historyDao);
                        log.info("History Annotation : {}", changedString);
                    }
                } else {
                    historyDao.setSeq(historyRepository.getAllData().size());
                    historyDao.setChangeData(userDao.getUserId() + " 신규추가");
                    historyDao.setLocalDateTime(LocalDateTime.now());
                    historyRepository.addHistory(historyDao);
                }
            }
        }

        return ret;
    }

    //@Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.TimerAnnotation)")
    @Around("within(kr.pe.yoonsm.history.aop.services.*)")
    public Object addTimer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long before = System.currentTimeMillis();
        Object ret = proceedingJoinPoint.proceed();
        log.info(">> time  annotation : {}", System.currentTimeMillis() - before);
        return ret;
    }
}

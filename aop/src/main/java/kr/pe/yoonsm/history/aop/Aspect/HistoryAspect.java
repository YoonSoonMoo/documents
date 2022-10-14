package kr.pe.yoonsm.history.aop.Aspect;

import kr.pe.yoonsm.history.aop.repository.HistoryRepository;
import kr.pe.yoonsm.history.aop.repository.UserRepository;
import kr.pe.yoonsm.history.aop.repository.dao.HistoryDao;
import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
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

    @Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.UserHistoryAnnotation)")
    public Object addHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // Request 에서 전달받은 원본값
        UserDao userDao = (UserDao) Arrays.stream(proceedingJoinPoint.getArgs())
                .sequential()
                .filter(data -> (data instanceof UserDao)).findFirst().orElse(null);

        if(userDao != null ) {
            log.info("Parameter values {}", userDao);
            UserDao userDaoDb = userRepository.findByUserId(userDao.getUserId());
            HistoryDao historyDao = new HistoryDao();
            if (userDaoDb != null) {
                String changedString = diff(userDaoDb, userDao, UserDao.class);
                if (changedString.length() > 0) {
                    historyDao.setSeq(historyRepository.getAllData().size());
                    historyDao.setChangeData(changedString);
                    historyDao.setLocalDateTime(LocalDateTime.now());
                    historyRepository.addHistory(historyDao);
                    log.info("History Annotation : {}", changedString);
                }
            } else {
                historyDao.setSeq(historyRepository.getAllData().size());
                historyDao.setChangeData("신규추가");
                historyDao.setLocalDateTime(LocalDateTime.now());
                historyRepository.addHistory(historyDao);
            }
        }
        Object ret = proceedingJoinPoint.proceed();
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

    public <T> String diff(T target1, T target2, Class<T> targetClass) {
        StringBuilder sb = new StringBuilder();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(targetClass, Object.class).getPropertyDescriptors()) {
                Object value1 = pd.getReadMethod().invoke(target1);
                Object value2 = pd.getReadMethod().invoke(target2);

                boolean isEqualValue = (value1 == value2) || (value1 != null && value1.equals(value2));
                // 같지 않은 값이 있다면
                if (!isEqualValue) {
                    sb.append(pd.getName()).append(" changed ").append(value1).append("->").append(value2);
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}

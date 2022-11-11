package kr.pe.yoonsm.history.aop.services;

import kr.pe.yoonsm.history.aop.Aspect.TimerAnnotation;
import kr.pe.yoonsm.history.aop.Aspect.UserHistoryAnnotation;
import kr.pe.yoonsm.history.aop.repository.HistoryRepository;
import kr.pe.yoonsm.history.aop.repository.UserRepository;
import kr.pe.yoonsm.history.aop.repository.dao.HistoryDao;
import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserDBService {   //---> AOP 의 대상이 되는 클래스를 target 이라 한다.

    final HistoryRepository historyRepository;
    final UserRepository userRepository;

    @UserHistoryAnnotation
    @TimerAnnotation // -> pointcut 을 within 으로 대체하면 쓸일이 없다.
    public boolean addUser(UserDao userDao) {
        userRepository.insertData(userDao);
        log.info("유저를 추가했습니다. | UserService.addUser : {}", userDao);
        return true;
    }

    public UserDao getUserInfoByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @UserHistoryAnnotation
    public boolean editUser(UserDao userDao) { // --> JoinPoint Advice 대상이 되는 메소드
        // 기존DB에 저장되어 있는 유저의 객체
        UserDao indbUser = userRepository.findByUserId(userDao.getUserId());
        if (indbUser != null) {
            indbUser.setUserName(userDao.getUserName());
            indbUser.setAddress1(userDao.getAddress1());
            indbUser.setAddress2(userDao.getAddress2());
            indbUser.setAge(userDao.getAge());
        }
        log.info("유저를 수정했습니다. | UserService.editUser : {}", indbUser);
        return true;
    }

    public List<HistoryDao> getAllHistory() {
        return historyRepository.getAllData();
    }

}

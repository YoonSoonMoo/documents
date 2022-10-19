package kr.pe.yoonsm.history.aop.services;

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
 *
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService {   //---> AOP 의 대상이 되는 클래스를 target 이라 한다.

    final HistoryRepository historyRepository;
    final UserRepository userRepository;

    @UserHistoryAnnotation
    //  @TimerAnnotation -> point cut으로 설정
    public boolean addUser(UserDao userDao) {
        userRepository.insertData(userDao);
        log.info("유저를 추가했습니다. : {}", userDao);
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
        return true;
    }

    public List<HistoryDao> getAllHistory() {
        return historyRepository.getAllData();
    }

}

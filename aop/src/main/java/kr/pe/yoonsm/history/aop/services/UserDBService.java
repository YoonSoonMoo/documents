package kr.pe.yoonsm.history.aop.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pe.yoonsm.history.aop.Aspect.TimerAnnotation;
import kr.pe.yoonsm.history.aop.Aspect.UserHistoryDBAnnotation;
import kr.pe.yoonsm.history.aop.repository.HistoryDataJpaRepository;
import kr.pe.yoonsm.history.aop.repository.UserDataJpaRepository;
import kr.pe.yoonsm.history.aop.repository.dao.HistoryEntity;
import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import kr.pe.yoonsm.history.aop.repository.dao.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserDBService {   //---> AOP 의 대상이 되는 클래스를 target 이라 한다.

    final HistoryDataJpaRepository historyDataJpaRepository;
    final UserDataJpaRepository userDataJpaRepository;

    final ObjectMapper objectMapper;

    @UserHistoryDBAnnotation
    @TimerAnnotation // -> pointcut 을 within 으로 대체하면 쓸일이 없다.
    public boolean addUser(UserDao userDao) {

        // 입력된 객체를 entity객체로 변경해 줘야 한다.
        UserEntity userEntity = objectMapper.convertValue(userDao,UserEntity.class);

        userDataJpaRepository.saveAndFlush(userEntity);
        log.info("유저를 추가했습니다. | UserService.addUser : {}", userEntity);
        return true;
    }

    public UserEntity getUserInfoByUserId(String userId) {
        return userDataJpaRepository.findByUserId(userId);
    }

    @UserHistoryDBAnnotation
    public boolean editUser(UserDao userDao) { // --> JoinPoint Advice 대상이 되는 메소드
        // 기존DB에 저장되어 있는 유저의 객체
        UserEntity indbUser = userDataJpaRepository.findByUserId(userDao.getUserId());
        if (indbUser != null) {
            indbUser.setUserName(userDao.getUserName());
            indbUser.setAddress1(userDao.getAddress1());
            indbUser.setAddress2(userDao.getAddress2());
            indbUser.setAge(userDao.getAge());
        }
        log.info("유저를 수정했습니다. | UserService.editUser : {}", indbUser);
        return true;
    }

    @Cacheable(cacheNames = "findUserHistoryCache" )
    public List<HistoryEntity> getAllHistory() {
        return historyDataJpaRepository.findAll();
    }

    @CacheEvict(cacheNames ="findUserHistoryCache",allEntries = true)
    public void clearCache(){
        log.info("cache clear!!");
    }
}

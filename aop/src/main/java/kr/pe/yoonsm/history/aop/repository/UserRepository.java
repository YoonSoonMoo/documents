package kr.pe.yoonsm.history.aop.repository;

import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Repository
@Slf4j
public class UserRepository {

    private List<UserDao> userDb = new ArrayList<>();

    public UserDao findByUserId(String userId) {
        log.info("UserDB count : {}" , userDb.size());

        for(UserDao userDao :  userDb){
            if(userDao.getUserId().equals(userId)){
                return userDao;
            }
        }
        return null;
    }

    /**
     * 입력한 이름으로 시작하는 이름 리스트를 리턴한다.
     *
     * @param userName
     * @return
     */
    public List<UserDao> findHistoryByUserName(String userName) {
        return userDb.stream().filter(data -> (data.getUserName().startsWith(userName))).collect(Collectors.toList());
    }

    public List<UserDao> getAlldata() {
        return userDb;
    }

    public void insertData(UserDao userDao) {
        userDb.add(userDao);
    }

}

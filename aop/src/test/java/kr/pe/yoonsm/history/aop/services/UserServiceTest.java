package kr.pe.yoonsm.history.aop.services;

import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-18
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    public UserDao createUserDao() {
        UserDao userDao = new UserDao();
        userDao.setUserId("yoonsm");
        userDao.setUserName("윤순무");
        userDao.setAge(48);
        userDao.setAddress1("강동구");
        userDao.setAddress2("둔촌2동 98");
        return userDao;
    }

    @Test
    @DisplayName("유저 등록 테스트 , AOP 추가 검증")
    void addUser() {
        assertEquals(userService.addUser(createUserDao()), true);
        assertEquals(userService.getAllHistory().get(0).getChangeData() ,  "yoonsm 신규추가");
    }

    @Test
    @DisplayName("유저 수정 테스트 , AOP 추가 검증")
    void editUser() {
        // 유저 등록
        userService.addUser(createUserDao());
        // 수정
        UserDao newUserDaou = createUserDao();

        // 이름을 변경
        newUserDaou.setUserName("윤유림");
        assertEquals(userService.editUser(newUserDaou),true);
        assertEquals(userService.getAllHistory().get(1).getChangeData() ,  "userName changed 윤순무->윤유림 ");

        // 나이를 변경
        newUserDaou.setAge(18);
        assertEquals(userService.editUser(newUserDaou),true);
        assertEquals(userService.getAllHistory().get(2).getChangeData() ,  "age changed 48->18 ");

        // 주소1 , 주소2 를 변경
        newUserDaou.setAddress1("군포시");
        newUserDaou.setAddress2("오금로");
        assertEquals(userService.editUser(newUserDaou),true);
        assertEquals(userService.getAllHistory().get(3).getChangeData() ,  "address1 changed 강동구->군포시 address2 changed 둔촌2동 98->오금로 ");
    }
}
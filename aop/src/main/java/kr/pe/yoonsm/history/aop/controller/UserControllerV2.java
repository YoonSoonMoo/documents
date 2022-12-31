package kr.pe.yoonsm.history.aop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pe.yoonsm.history.aop.repository.dao.UserDao;
import kr.pe.yoonsm.history.aop.repository.dao.UserEntity;
import kr.pe.yoonsm.history.aop.services.UserDBService;
import kr.pe.yoonsm.history.aop.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@RestController
@RequestMapping("/api/v2")
@Slf4j
@AllArgsConstructor
public class UserControllerV2 {

    final UserDBService userDBService;
    final ObjectMapper objectMapper;

    @ResponseBody
    @RequestMapping(value = "/user/{userId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String getUserInfo(@PathVariable("userId") String userId) {
        UserEntity userDao = userDBService.getUserInfoByUserId(userId);
        if (userDao == null) return "유저가 없습니다.";
        return userDao.toString();
    }

    /**
     * 유저를 등록 ( aop 로 유저 히스토리에 내역을 저장 : 신규등록 )
     *
     * @param userDao
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/adduser", method = {RequestMethod.POST})
    public String addUser(@RequestBody UserDao userDao) {
        if (userDBService.getUserInfoByUserId(userDao.getUserId()) != null) {
            return "이미 있는 유저 입니다.";
        }
        userDBService.addUser(userDao);
        return "유저가 추가되었습니다 " + userDao;
    }


    /**
     * 유저 수정 ( aop 로 유저 히스토리에 내역을 저장 : 수정된 내용)
     *
     * @param userDao
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/edituser", method = {RequestMethod.POST})
    public String editUser(@RequestBody UserDao userDao) {
        if (userDBService.getUserInfoByUserId(userDao.getUserId()) == null) {
            return "수정할 유저가 없습니다.";
        }

        userDBService.editUser(userDao);
        return "유저를 수정했습니다.";
    }

    @ResponseBody
    @RequestMapping(value = "/history/all", method = {RequestMethod.GET})
    public String editUser() {

        StringBuilder returnValue = new StringBuilder();
        userDBService.getAllHistory().stream().forEach(data -> {
            returnValue.append(data.toString());
        });
        return returnValue.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/clear/cache", method = {RequestMethod.GET})
    public String clearCache() {

        userDBService.clearCache();
        return "OK";
    }


}

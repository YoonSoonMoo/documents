package kr.pe.yoonsm.history.aop.repository.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDao {
    private String userId;
    private String userName;
    private int age;
    private String address1;
    private String address2;

}

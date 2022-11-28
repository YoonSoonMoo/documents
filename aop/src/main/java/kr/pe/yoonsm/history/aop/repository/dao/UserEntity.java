package kr.pe.yoonsm.history.aop.repository.dao;

import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-11
 */
@Entity(name="YS_USER")
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String userId;
    private String userName;
    private int age;
    private String address1;
    private String address2;
}

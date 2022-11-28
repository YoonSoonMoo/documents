package kr.pe.yoonsm.history.aop.repository.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-11
 */
@Entity
public class HistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seq;

    private String ChangeData;
    private LocalDateTime localDateTime;

}
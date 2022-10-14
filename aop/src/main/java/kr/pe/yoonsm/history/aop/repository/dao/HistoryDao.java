package kr.pe.yoonsm.history.aop.repository.dao;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Data
public class HistoryDao {

    private int seq;
    private String ChangeData;
    private LocalDateTime localDateTime;
}

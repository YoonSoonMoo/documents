package kr.pe.yoonsm.history.aop.repository.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-11
 */
@Entity(name="YS_HISTORY")
@Setter
@Getter
@ToString
public class HistoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seq;
    @Getter
    private String ChangeData;
    private LocalDateTime localDateTime;

}

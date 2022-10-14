package kr.pe.yoonsm.history.aop.repository;

import kr.pe.yoonsm.history.aop.repository.dao.HistoryDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoonsm@daou.co.kr on 2022-10-11
 */
@Repository
@Slf4j
public class HistoryRepository {

    private List<HistoryDao> historyDaoList = new ArrayList<>();

    public void addHistory(HistoryDao historyDao) {
        historyDaoList.add(historyDao);
    }

    public List<HistoryDao> getAllData(){
        return historyDaoList;
    }

}

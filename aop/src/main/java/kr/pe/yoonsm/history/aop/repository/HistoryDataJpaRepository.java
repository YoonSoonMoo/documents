package kr.pe.yoonsm.history.aop.repository;

import kr.pe.yoonsm.history.aop.repository.dao.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryDataJpaRepository extends JpaRepository<HistoryEntity, Long> {
}

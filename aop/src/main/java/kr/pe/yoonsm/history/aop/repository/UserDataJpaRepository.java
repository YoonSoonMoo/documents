package kr.pe.yoonsm.history.aop.repository;

import kr.pe.yoonsm.history.aop.repository.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataJpaRepository extends JpaRepository<UserEntity, Long > {
    UserEntity findByUserId(String userId);
}

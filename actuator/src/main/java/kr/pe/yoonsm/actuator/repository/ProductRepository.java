package kr.pe.yoonsm.actuator.repository;

import kr.pe.yoonsm.actuator.repository.entity.Product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Product findByProductCode(String productCode);
    List<Product> findByProductName(String productName);
}
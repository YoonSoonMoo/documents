package kr.pe.yoonsm.actuator.repository;

import kr.pe.yoonsm.actuator.repository.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findByProductId(String id);

    List<Product> findByProductName(String productName);
}
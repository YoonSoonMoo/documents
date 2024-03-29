package kr.pe.yoonsm.actuator.repository;

import kr.pe.yoonsm.actuator.repository.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
    Optional<List<Product>> findByProductName(String productName);

    Optional<List<Product>> findByProductNameLike(String productName);
}
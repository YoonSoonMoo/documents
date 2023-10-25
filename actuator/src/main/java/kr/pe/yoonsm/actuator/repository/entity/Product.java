package kr.pe.yoonsm.actuator.repository.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "product")
@Getter
public class Product {

    @Id
    private String id;
    @Indexed
    private String productName;
    int quantity;
    int price;
}

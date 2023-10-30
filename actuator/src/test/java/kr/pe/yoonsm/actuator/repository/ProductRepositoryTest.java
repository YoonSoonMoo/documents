package kr.pe.yoonsm.actuator.repository;

import kr.pe.yoonsm.actuator.repository.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ProductRepositoryTest {

    @Autowired
    private ProductRepository redisRepository;

    @Test
    void saveAndsearch() throws Exception {
        // given
        Product newProduct = Product.builder()
                .id("test001")
                .productName("테스트 양말")
                .price(100)
                .quantity(5)
                .build();

        // when
        Product save = redisRepository.save(newProduct);

        // then
        Product find = redisRepository.findById(save.getId()).get();
        log.info("상품정보 : {}", find);

        Assertions.assertEquals("test001", find.getId());
    }
}
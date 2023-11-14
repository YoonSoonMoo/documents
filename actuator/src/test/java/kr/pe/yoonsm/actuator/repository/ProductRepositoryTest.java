package kr.pe.yoonsm.actuator.repository;

import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.repository.entity.Product;
import kr.pe.yoonsm.actuator.service.ProductRedisTempService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class ProductRepositoryTest {

    @Autowired
    private ProductRepository redisRepository;

    @Autowired
    private ProductRedisTempService productRedisTempService;

    @Test
    @DisplayName("상품 저장과 검색")
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

    @Test
    @DisplayName("like 검색으로 상품조회")
    void likeSearch() {
        CommonResponse<List<Product>> response = productRedisTempService.findProductByProductNameLike("마스카라");
        log.debug("검색된 건수  : {}" , response.getData().size());
        Assertions.assertEquals(response.getResult(), "200");
        //Assertions.assertEquals(response.getData().size(), 1443);
    }


    @Test
    @DisplayName("상품 풀명칭으로 조회")
    void searchProduct() {
        CommonResponse<List<Product>> response = productRedisTempService.findProductByProductName("순무마스카라");
        log.debug("검색된 건수  : {}" , response.getData().size());
        Assertions.assertEquals(response.getResult(), "200");
        //Assertions.assertEquals(response.getData().size(), 1443);
    }

    @Test
    @DisplayName("RedisDataJPA에 없는 키워드 like")
    void searchProductFromDataRedis(){
        Assertions.assertThrows(IllegalArgumentException.class,()-> redisRepository.findByProductNameLike("마스카라"));
    }
}
package kr.pe.yoonsm.actuator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import kr.pe.yoonsm.actuator.repository.entity.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ProductRedisTempService {

    // 상품용
    @Autowired
    RedisTemplate<String, Product> redisTemplate;

    // 인덱스용 ( smembers )
    @Qualifier("redisIndexTemplate")
    RedisTemplate<String, String> redisIndexTemplate;

    final ObjectMapper objectMapper;

//    @Resource(name = "redisTemplate")
//    private ValueOperations<String, Product> valueOperations;
//
//    @Resource(name = "redisIndexTemplate")
//    private SetOperations<String, String> redisIndexTemplate;

    @Counted("my.redisTemp.product")
    public CommonResponse<String> addProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();
        Product product = Product.builder()
                .id(productRequest.getId())
                .productName(productRequest.getProductName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity()).build();

        SetOperations<String, String> productIndex = redisIndexTemplate.opsForSet();
        redisTemplate.opsForValue().set(productRequest.getId(), product);
        // 상품명으로 ID를 찾는 인덱스 추가
        productIndex.add("PRODUCT_NAME:" + productRequest.getProductName(), productRequest.getId());
        // ID로 를 찾는 인덱스 추가
        productIndex.add("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());

        commonResponse.setResult("200");
        commonResponse.setData(product.getProductName());
        return commonResponse;
    }

    @Counted("my.redisTemp.product")
    public CommonResponse<String> updateProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();
        Product result = redisTemplate.opsForValue().get(productRequest.getId());

        if (result == null) {
            commonResponse.setResult("900");
        } else {

            Product product = Product.builder()
                    .id(productRequest.getId())
                    .productName(productRequest.getProductName())
                    .price(productRequest.getPrice())
                    .quantity(productRequest.getQuantity())
                    .build();
            redisTemplate.opsForValue().set(productRequest.getId(), product);

            // 기존의 인덱스는 삭제한다.
            redisIndexTemplate.opsForSet().remove("PRODUCT_NAME:" + result.getProductName(), productRequest.getId());
            redisIndexTemplate.opsForSet().remove("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());


            // 상품명으로 ID를 찾는 인덱스 추가
            redisIndexTemplate.opsForSet().add("PRODUCT_NAME:" + productRequest.getProductName(), productRequest.getId());
            // ID로 를 찾는 인덱스 추가
            redisIndexTemplate.opsForSet().add("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());

            commonResponse.setResult("200");
        }
        return commonResponse;
    }

    @Counted("my.redisTemp.product")
    public CommonResponse<Product> findProductById(String id) {
        CommonResponse commonResponse = new CommonResponse();
        Product result = redisTemplate.opsForValue().get(id);

        if (result == null) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(result);
        }
        return commonResponse;
    }

    //@Counted("my.redisTemp.product")
    @Timed("my.redisTemp.timed")
    public CommonResponse<List<Product>> findProductByProductName(String productName) {
        CommonResponse commonResponse = new CommonResponse();
        List<Product> returnList = new ArrayList<>();

        // smember 에서 인덱싱된 상품명을 검색한다.
        Set<String> resultList = redisIndexTemplate.opsForSet().members("PRODUCT_NAME:" + productName);

        // 추출된 키로 실 데이타를 검색한다.
        for (String key : resultList.stream().toList()) {
            log.debug("인덱스 검색된 키 : {}", key);
            returnList.add(redisTemplate.opsForValue().get(key));
        }

        if (resultList.isEmpty()) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(returnList);
        }
        return commonResponse;
    }


    @Timed("my.redisTemp.timed")
    public CommonResponse<List<Product>> findProductByProductNameLike(String productName) {

        CommonResponse commonResponse = new CommonResponse();
        List<Product> returnList = new ArrayList<>();

        ScanOptions scanOptions = ScanOptions.scanOptions().match("*" + productName + "*").count(100).build();
        // smember 에서 인덱싱된 상품명을 검색한다.
        Cursor<String> keys = redisIndexTemplate.scan(scanOptions);

        while (keys.hasNext()) {
            Set<String> resultList = redisIndexTemplate.opsForSet().members(new String(keys.next()));
            for (String key : resultList.stream().toList()) {
                Product product = redisTemplate.opsForValue().get(key);
                //log.debug("인덱스 검색된 키 : {} , 상품명 : {}", key , product.getProductName());
                returnList.add(product);
            }
        }

        if (returnList.isEmpty()) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(returnList);
        }

        return commonResponse;
    }

}

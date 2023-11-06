package kr.pe.yoonsm.actuator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.annotation.Counted;
import jakarta.annotation.Resource;
import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import kr.pe.yoonsm.actuator.repository.entity.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
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
    RedisTemplate<String,Product> redisTemplate;

    // 인덱스용 ( smembers )
    @Qualifier("redisIndexTemplate")
    RedisTemplate<String,String> redisIndexTemplate;

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

        redisTemplate.delete(productRequest.getId());
        ValueOperations<String,Product> valueOperations = redisTemplate.opsForValue();
        SetOperations<String, String> productIndex = redisIndexTemplate.opsForSet();

        valueOperations.set(productRequest.getId(), product);
        // 인덱스에 있는 내용을 삭제한다.
        //productIndex.remove("PRODUCT_NAME:"+productRequest.getProductName(),productRequest.getId());

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

        ValueOperations<String,Product> valueOperations = redisTemplate.opsForValue();
        SetOperations<String, String> productIndex = redisIndexTemplate.opsForSet();
        Product result = (Product) valueOperations.get(productRequest.getId());

        if (result == null) {
            commonResponse.setResult("900");
        } else {

            Product product = Product.builder()
                    .id(productRequest.getId())
                    .productName(productRequest.getProductName())
                    .price(productRequest.getPrice())
                    .quantity(productRequest.getQuantity())
                    .build();
            valueOperations.set(productRequest.getId(), product);

            // 기존의 인덱스는 삭제한다.
            productIndex.remove("PRODUCT_NAME:" + result.getProductName(), productRequest.getId());
            productIndex.remove("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());

            // 상품명으로 ID를 찾는 인덱스 추가
            productIndex.add("PRODUCT_NAME:" + productRequest.getProductName(), productRequest.getId());
            // ID로 를 찾는 인덱스 추가
            productIndex.add("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());

            commonResponse.setResult("200");
        }
        return commonResponse;
    }

    @Counted("my.redisTemp.product")
    public CommonResponse<Product> findProductById(String id) {
        CommonResponse commonResponse = new CommonResponse();

        //ValueOperations<String,Product> valueOperations = redisTemplate.opsForValue();
        //Product result = valueOperations.get(id);
        Product result = redisTemplate.opsForValue().get(id);

        if (result == null) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(result);
        }
        return commonResponse;
    }

    @Counted("my.redisTemp.product")
    public CommonResponse<List<Product>> findProductByProductName(String productName) {
        CommonResponse commonResponse = new CommonResponse();
        List<Product> returnList = new ArrayList<>();
        ValueOperations<String,Product> valueOperations = redisTemplate.opsForValue();
        SetOperations<String, String> productIndex = redisIndexTemplate.opsForSet();

        Set<String> resultList = productIndex.members("PRODUCT_NAME:"+productName);
        for(String key : resultList.stream().toList()){

            log.debug("인덱스 검색된 키 : {}",key);

            Product product = (Product) valueOperations.get(key);
            returnList.add(product);
        }

        if (resultList.isEmpty()) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(returnList);
        }
        return commonResponse;
    }

//    public <T> T getData(String key , Class<T> classType ) throws Exception{
//        String jsonResult = redisTemplate.opsForValue().get(key);
//        if(StringUtils.isBlank(jsonResult)){
//            return null;
//        } else{
//            ObjectMapper mapper = new ObjectMapper();
//            T obj = mapper.readValue(jsonResult,classType);
//            return obj;
//        }
//    }
}

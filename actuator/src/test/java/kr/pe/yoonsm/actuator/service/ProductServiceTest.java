package kr.pe.yoonsm.actuator.service;

import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
class ProductServiceTest {

    String[] inputData = {"순무양말", "순무덧신", "순무신발", "순무마스카라", "순무립스틱", "순무잠바", "순무스카프"};
    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("RedisDataJPA를 사용하여 상품등록")
    public void productSaveV1_test() {
        // 10000등록 2분36초 소요
        int LOOP_COUNT = 10000;
        String url = "http://localhost:8080/v1/orders/addProduct";
        Random random = new Random();

        // 100개의 데이터 생성
        for (int i = 0; i < LOOP_COUNT; i++) {
            // 랜덤으로 한 개의 데이터 선택
            int index = random.nextInt(inputData.length);

            ProductRequest productRequest = new ProductRequest();
            productRequest.setId("a" + String.format("%04d", i));
            productRequest.setProductName(inputData[index]);
            productRequest.setPrice(1200);
            productRequest.setQuantity(3);
            HttpEntity<ProductRequest> httpEntity = new HttpEntity<>(productRequest);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
            // 만들어진 데이타 표시
            System.out.println(response);
        }
    }

    @Test
    @DisplayName("RedisTemplate를 사용하여 상품등록")
    public void productSaveV2_test() {
        int LOOP_COUNT = 10000;
        String url = "http://localhost:8080/v2/orders/addProduct";
        Random random = new Random();

        // 10000등록 2분38초 소요
        // 100개의 데이터 생성
        for (int i = 0; i < LOOP_COUNT; i++) {
            // 랜덤으로 한 개의 데이터 선택
            int index = random.nextInt(inputData.length);

            ProductRequest productRequest = new ProductRequest();
            productRequest.setId("a" + String.format("%04d", i));
            productRequest.setProductName(inputData[index]);
            productRequest.setPrice(1200);
            productRequest.setQuantity(3);

            HttpEntity<ProductRequest> httpEntity = new HttpEntity<>(productRequest);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
            // 만들어진 데이타 표시
            System.out.println(response);
        }
    }

    @Test
    @DisplayName("레디스 상품검색 RedisDataJPA")
    public void findProductByNameV1_test() {
        Random random = new Random();
        int index = random.nextInt(inputData.length);
        // Given
        String productName = inputData[index];

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                String.format("http://localhost:8080/v1/orders/products/name/%s", productName),
                HttpMethod.GET,
                null,
                String.class
        );

        System.out.println(response);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("레디스 상품검색 RedisTemplate")
    public void findProductbyNameV2_test() {

        Random random = new Random();
        int index = random.nextInt(inputData.length);
        // Given
        String productName = inputData[index];

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                String.format("http://localhost:8080/v1/orders/products/name/%s", productName),
                HttpMethod.GET,
                null,
                String.class
        );

        System.out.println(response.toString());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DataRedis 대량 상품명으로 검색")
    public void findProductByNameMultiV1_test() {
        // 1000건 검색에 2분 2초
        // 10건 검색에 1초 31
        // 10건 검색에 1초 62
        // 100건 검색에 11초 67

        for (int i = 0; i < 100; i++) {
            findProductByNameV1_test();
        }
    }

    @Test
    @DisplayName("RedisTemplate 대량 상품명으로 검색")
    public void findProductByNameMultiV2_test() {
        // 1000건 검색 2분 4초
        // 10건 검색에 1초 29
        // 10건 검색에 1초 43
        // 100 검색에 11초 39

        for (int i = 0; i < 100; i++) {
            findProductbyNameV2_test();
        }
    }

}
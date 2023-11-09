package kr.pe.yoonsm.actuator.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
class ProductServiceTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    public void getOrderProductsByName() {
        // Given
        String productName = "순무장갑";

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                String.format("http://localhost:8080/v1/orders/products/name/%s", productName),
                HttpMethod.GET,
                null,
                String.class
        );
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void multiOrderProductByName() {
        for (int i = 0; i < 1000; i++) {
            getOrderProductsByName();
        }
    }


}
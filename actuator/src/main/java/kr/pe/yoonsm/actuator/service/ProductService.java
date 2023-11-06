package kr.pe.yoonsm.actuator.service;

import io.micrometer.core.annotation.Counted;
import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import kr.pe.yoonsm.actuator.repository.ProductRepository;
import kr.pe.yoonsm.actuator.repository.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    final ProductRepository productRepository;

    @Counted("my.product")
    public CommonResponse<String> addProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();
        Product product = Product.builder()
                .id(productRequest.getId())
                .productName(productRequest.getProductName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity()).build();
        productRepository.save(product);

        commonResponse.setResult("200");
        commonResponse.setData(product.getProductName());
        return commonResponse;
    }

    @Counted("my.product")
    public CommonResponse<String> updateProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();

        Optional<Product> result = productRepository.findById(productRequest.getId());
        if (result.isEmpty()) {
            commonResponse.setResult("900");
        } else {
            Product product = Product.builder()
                    .id(productRequest.getId())
                    .productName(productRequest.getProductName())
                    .price(productRequest.getPrice())
                    .quantity(productRequest.getQuantity())
                    .build();
            productRepository.save(product);
            commonResponse.setResult("200");
        }
        return commonResponse;
    }

    @Counted("my.product")
    public CommonResponse<Product> findProductById(String id) {
        CommonResponse commonResponse = new CommonResponse();
        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(product.get());
        }
        return commonResponse;
    }

    @Counted("my.product")
    public CommonResponse<List<Product>> findProductByProductName(String productName) {
        CommonResponse commonResponse = new CommonResponse();
        Optional<List<Product>> productList = productRepository.findByProductName(productName);

        if (productList.isEmpty()) {
            commonResponse.setResult("901");
        } else {
            commonResponse.setResult("200");
            commonResponse.setData(productList.get());
        }
        return commonResponse;
    }
}

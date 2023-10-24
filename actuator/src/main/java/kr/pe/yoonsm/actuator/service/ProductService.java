package kr.pe.yoonsm.actuator.service;

import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import kr.pe.yoonsm.actuator.repository.ProductRepository;
import kr.pe.yoonsm.actuator.repository.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    final ProductRepository productRepository;

    public CommonResponse<String> addProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();
        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        productRepository.save(product);

        commonResponse.setResult("200");
        commonResponse.setData(product.getProductName());
        return commonResponse;
    }

    public CommonResponse<String> updateProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();

        Product result = productRepository.findByProductCode(productRequest.getProductCode());
        if (result == null) {
            commonResponse.setResult("900");
        } else {
            BeanUtils.copyProperties(productRequest, result);
            productRepository.save(result);
            commonResponse.setResult("200");
        }
        return commonResponse;
    }

}

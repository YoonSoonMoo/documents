package kr.pe.yoonsm.actuator.controller;

import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import kr.pe.yoonsm.actuator.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@Slf4j
@AllArgsConstructor
public class OrderController {

    final ProductService productService;

    @PostMapping("/addProduct")
    public CommonResponse addProduct(@RequestBody ProductRequest productRequest) {
        log.debug("주문내용 : " + productRequest.toString());
        return productService.addProductProcess(productRequest);
    }

    @PostMapping("/updateProduct")
    public CommonResponse updateProduct(@RequestBody ProductRequest productRequest){
        log.debug("주문내용 갱신" + productRequest.toString());
        return productService.updateProductProcess(productRequest);
    }

    @GetMapping("/products/{id}")
    public CommonResponse findProduct(@PathVariable String id){
        log.debug("상품 검색 : {}",id);
        return productService.findProductById(id);
    }

    @GetMapping("/products/name/{productName}")
    public CommonResponse findProductByName(@PathVariable String productName){
        log.debug("상품명 검색 : {}",productName);
        return productService.findProductByProductName(productName);
    }


}

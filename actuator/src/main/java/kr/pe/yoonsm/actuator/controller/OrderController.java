package kr.pe.yoonsm.actuator.controller;

import kr.pe.yoonsm.actuator.controller.vo.CommonResponse;
import kr.pe.yoonsm.actuator.controller.vo.ProductRequest;
import kr.pe.yoonsm.actuator.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
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
        return productService.addProductProcess(productRequest);
    }

}

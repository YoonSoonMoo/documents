package kr.pe.yoonsm.actuator.controller.vo;

import lombok.Data;

@Data
public class ProductRequest {
    private String productCode;
    private String productName;
    private int quantity;
}

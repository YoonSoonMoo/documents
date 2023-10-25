package kr.pe.yoonsm.actuator.controller.vo;

import lombok.Data;

@Data
public class ProductRequest {
    private String id;
    private String productName;
    private int quantity;
    private int price;
}

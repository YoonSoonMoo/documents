package kr.pe.yoonsm.actuator.controller.vo;

import lombok.Data;

@Data
public class CommonResponse<T> {
    private String result;
    private T data;
}

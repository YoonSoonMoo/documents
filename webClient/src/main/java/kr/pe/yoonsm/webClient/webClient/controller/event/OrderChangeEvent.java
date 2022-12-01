package kr.pe.yoonsm.webClient.webClient.controller.event;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-30
 */
public class OrderChangeEvent {

    String orderNo = "";
    public OrderChangeEvent(String orderNo){
        this.orderNo = orderNo;
    }
    public String getOrderNo(){
        return orderNo;
    }
}

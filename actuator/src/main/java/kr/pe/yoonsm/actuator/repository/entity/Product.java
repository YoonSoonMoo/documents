package kr.pe.yoonsm.actuator.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    private String productCode;
    private String productName;
    int quantity;
    int price;

    public Long getId() {
        return id;
    }
}

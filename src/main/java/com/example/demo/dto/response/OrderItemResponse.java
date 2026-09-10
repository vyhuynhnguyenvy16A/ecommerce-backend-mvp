package com.example.demo.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponse {

    private Long id;
    private String productNameSnapshot;
    private String skuSnapshot;
    private String sizeSnapshot;
    private String colorSnapshot;
    private BigDecimal unitPriceSnapshot;
    private Integer quantity;
    private BigDecimal subtotal;

    public BigDecimal getSubtotal() {
        if (unitPriceSnapshot == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return unitPriceSnapshot.multiply(BigDecimal.valueOf(quantity));
    }
}

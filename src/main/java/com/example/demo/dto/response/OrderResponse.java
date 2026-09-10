package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {

    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private String shippingRecipientName;
    private String shippingPhone;
    private String shippingAddressLine;
    private String shippingCity;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}

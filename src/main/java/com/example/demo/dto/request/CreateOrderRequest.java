package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "Shipping recipient name is required")
    private String shippingRecipientName;

    @NotBlank(message = "Shipping phone is required")
    private String shippingPhone;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddressLine;

    @NotBlank(message = "Shipping city is required")
    private String shippingCity;
}

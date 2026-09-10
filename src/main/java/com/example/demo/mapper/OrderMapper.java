package com.example.demo.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.dto.response.OrderItemResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductNameSnapshot(item.getProductNameSnapshot());
        response.setSkuSnapshot(item.getSkuSnapshot());
        response.setSizeSnapshot(item.getSizeSnapshot());
        response.setColorSnapshot(item.getColorSnapshot());
        response.setUnitPriceSnapshot(item.getUnitPriceSnapshot());
        response.setQuantity(item.getQuantity());
        return response;
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingRecipientName(order.getShippingRecipientName());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingAddressLine(order.getShippingAddressLine());
        response.setShippingCity(order.getShippingCity());
        response.setItems(itemResponses);
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}

package com.example.demo.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CreateOrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductVariant;
import com.example.demo.entity.User;
import com.example.demo.exception.EmptyCartException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Map<String, Set<String>> VALID_STATUS_TRANSITIONS = Map.of(
            "PENDING", Set.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED", Set.of("SHIPPING", "CANCELLED"),
            "SHIPPING", Set.of("DELIVERED"),
            "DELIVERED", Set.of(),
            "CANCELLED", Set.of());

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderMapper orderMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public OrderResponse checkout(Long userId, CreateOrderRequest request, String idempotencyKey) {
        String redisKey = "idempotency:order:" + idempotencyKey;
        String existingOrderId = redisTemplate.opsForValue().get(redisKey);
        if (existingOrderId != null) {
            Order existingOrder = orderRepository.findById(Long.valueOf(existingOrderId))
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            return orderMapper.toResponse(existingOrder);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EmptyCartException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            int quantity = cartItem.getQuantity();

            if (variant.getStockQuantity() < quantity) {
                throw new InsufficientStockException("Not enough stock for variant: " + variant.getSku());
            }

            variant.setStockQuantity(variant.getStockQuantity() - quantity);
            productVariantRepository.save(variant);

            Product product = variant.getProduct();
            BigDecimal unitPrice = variant.getPriceOverride() != null
                    ? variant.getPriceOverride()
                    : product.getBasePrice();

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .productNameSnapshot(product.getName())
                    .skuSnapshot(variant.getSku())
                    .sizeSnapshot(variant.getSize())
                    .colorSnapshot(variant.getColor())
                    .unitPriceSnapshot(unitPrice)
                    .quantity(quantity)
                    .build();

            totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(user)
                .status("PENDING")
                .totalAmount(totalAmount)
                .shippingRecipientName(request.getShippingRecipientName())
                .shippingPhone(request.getShippingPhone())
                .shippingAddressLine(request.getShippingAddressLine())
                .shippingCity(request.getShippingCity())
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        redisTemplate.opsForValue().set(redisKey, savedOrder.getId().toString(), Duration.ofHours(24));

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        String normalizedStatus = newStatus.trim().toUpperCase(Locale.ROOT);
        Set<String> allowedStatuses = VALID_STATUS_TRANSITIONS.get(order.getStatus());
        if (allowedStatuses == null || !allowedStatuses.contains(normalizedStatus)) {
            throw new IllegalStateException(
                    "Cannot change order status from " + order.getStatus() + " to " + normalizedStatus);
        }

        order.setStatus(normalizedStatus);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this order");
        }

        return orderMapper.toResponse(order);
    }
}

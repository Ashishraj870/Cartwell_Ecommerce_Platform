package com.shop.dto;

import com.shop.entity.OrderStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dtos {
    public record RegisterReq(@NotBlank String name, @NotBlank @Email String email,
                              @Size(min = 6, message = "Password must be at least 6 characters") String password) {}
    public record LoginReq(@NotBlank String email, @NotBlank String password) {}
    public record AuthRes(String token, String name, String email, String role) {}

    public record CategoryReq(@NotBlank String name) {}
    public record CategoryRes(Long id, String name) {}

    public record ProductReq(@NotBlank String name, String description,
                             @NotNull @DecimalMin("0.01") BigDecimal price,
                             @Min(0) int stock, String imageUrl, @NotNull Long categoryId) {}
    public record ProductRes(Long id, String name, String description, BigDecimal price, int stock,
                             String imageUrl, Long categoryId, String categoryName) {}

    public record CartItemReq(@NotNull Long productId, @Min(1) int quantity) {}
    public record QtyReq(@Min(0) int quantity) {}
    public record CartItemRes(Long productId, String name, String imageUrl, BigDecimal price, int quantity, int stock) {}
    public record CartRes(List<CartItemRes> items, BigDecimal total, int count) {}

    public record CheckoutReq(@NotBlank(message = "Enter a shipping address") String shippingAddress) {}
    public record OrderItemRes(Long productId, String name, int quantity, BigDecimal unitPrice) {}
    public record OrderRes(Long id, String status, BigDecimal total, String shippingAddress, LocalDateTime createdAt,
                           String customerName, String customerEmail, List<OrderItemRes> items) {}
    public record StatusReq(@NotNull OrderStatus status) {}
}

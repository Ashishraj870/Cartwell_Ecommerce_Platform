package com.shop.dto;

import com.shop.dto.Dtos.*;
import com.shop.entity.*;
import java.math.BigDecimal;

public final class Mapper {
    private Mapper() {}

    public static ProductRes product(Product p) {
        return new ProductRes(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(),
                p.getImageUrl(), p.getCategory().getId(), p.getCategory().getName());
    }

    public static CartRes cart(Cart c) {
        var items = c.getItems().stream().map(i -> new CartItemRes(i.getProduct().getId(), i.getProduct().getName(),
                i.getProduct().getImageUrl(), i.getProduct().getPrice(), i.getQuantity(), i.getProduct().getStock())).toList();
        BigDecimal total = items.stream().map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int count = items.stream().mapToInt(CartItemRes::quantity).sum();
        return new CartRes(items, total, count);
    }

    public static OrderRes order(Order o) {
        var items = o.getItems().stream().map(i -> new OrderItemRes(i.getProduct().getId(), i.getProduct().getName(),
                i.getQuantity(), i.getUnitPrice())).toList();
        return new OrderRes(o.getId(), o.getStatus().name(), o.getTotal(), o.getShippingAddress(), o.getCreatedAt(),
                o.getUser().getName(), o.getUser().getEmail(), items);
    }
}

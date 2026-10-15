package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "order_items")
@Getter @Setter @NoArgsConstructor
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id") private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id") private Product product;
    @Column(nullable = false) private int quantity;
    /** Price snapshot at purchase time, so later price changes never rewrite history. */
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal unitPrice;
    public OrderItem(Order order, Product product, int quantity, BigDecimal unitPrice) {
        this.order = order; this.product = product; this.quantity = quantity; this.unitPrice = unitPrice;
    }
}

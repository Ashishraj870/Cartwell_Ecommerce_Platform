package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
@Getter @Setter @NoArgsConstructor
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cart_id") private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id") private Product product;
    @Column(nullable = false) private int quantity;
    public CartItem(Cart cart, Product product, int quantity) { this.cart = cart; this.product = product; this.quantity = quantity; }
}

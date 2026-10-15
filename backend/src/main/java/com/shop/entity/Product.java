package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "products", indexes = @Index(name = "idx_product_name", columnList = "name"))
@Getter @Setter @NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(length = 1000) private String description;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int stock;
    private String imageUrl;
    /** Soft delete: keeps order history intact when a product is removed from the catalog. */
    @Column(nullable = false) private boolean active = true;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id") private Category category;
}

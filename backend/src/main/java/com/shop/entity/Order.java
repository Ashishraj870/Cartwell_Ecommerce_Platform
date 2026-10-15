package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status = OrderStatus.PLACED;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total = BigDecimal.ZERO;
    @Column(nullable = false, length = 500) private String shippingAddress;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItem> items = new ArrayList<>();
    public Order(User user, String shippingAddress) { this.user = user; this.shippingAddress = shippingAddress; }
}

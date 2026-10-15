package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/** One cart per user, stored server-side so it follows the customer across devices. */
@Entity @Table(name = "carts")
@Getter @Setter @NoArgsConstructor
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", unique = true) private User user;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) private List<CartItem> items = new ArrayList<>();
    public Cart(User user) { this.user = user; }
}

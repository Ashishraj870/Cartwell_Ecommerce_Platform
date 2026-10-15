package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role = Role.CUSTOMER;

    public User(String name, String email, String password, Role role) {
        this.name = name; this.email = email; this.password = password; this.role = role;
    }
}

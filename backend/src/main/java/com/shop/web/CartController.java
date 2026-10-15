package com.shop.web;

import com.shop.dto.Dtos.*;
import com.shop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService service;
    public CartController(CartService service) { this.service = service; }

    @GetMapping public CartRes get(Authentication a) { return service.get(a.getName()); }

    @PostMapping("/items")
    public CartRes add(Authentication a, @Valid @RequestBody CartItemReq r) { return service.add(a.getName(), r); }

    @PutMapping("/items/{productId}")
    public CartRes setQty(Authentication a, @PathVariable Long productId, @Valid @RequestBody QtyReq r) {
        return service.setQuantity(a.getName(), productId, r.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public CartRes remove(Authentication a, @PathVariable Long productId) { return service.setQuantity(a.getName(), productId, 0); }

    @DeleteMapping public CartRes clear(Authentication a) { return service.clear(a.getName()); }
}

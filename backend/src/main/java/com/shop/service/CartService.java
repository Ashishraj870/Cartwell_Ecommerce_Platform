package com.shop.service;

import com.shop.dto.Dtos.*;
import com.shop.dto.Mapper;
import com.shop.entity.*;
import com.shop.repo.*;
import com.shop.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {
    private final CartRepository carts;
    private final ProductRepository products;
    private final UserRepository users;

    public CartService(CartRepository carts, ProductRepository products, UserRepository users) {
        this.carts = carts; this.products = products; this.users = users;
    }

    public CartRes get(String email) { return Mapper.cart(cartFor(email)); }

    public CartRes add(String email, CartItemReq req) {
        Cart cart = cartFor(email);
        Product p = activeProduct(req.productId());
        CartItem item = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(p.getId())).findFirst().orElse(null);
        int qty = (item == null ? 0 : item.getQuantity()) + req.quantity();
        checkStock(p, qty);
        if (item == null) cart.getItems().add(new CartItem(cart, p, qty)); else item.setQuantity(qty);
        return Mapper.cart(cart);
    }

    public CartRes setQuantity(String email, Long productId, int qty) {
        Cart cart = cartFor(email);
        if (qty <= 0) {
            cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        } else {
            CartItem item = cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst()
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item is not in your cart"));
            checkStock(item.getProduct(), qty);
            item.setQuantity(qty);
        }
        return Mapper.cart(cart);
    }

    public CartRes clear(String email) {
        Cart cart = cartFor(email);
        cart.getItems().clear();
        return Mapper.cart(cart);
    }

    private Cart cartFor(String email) {
        User user = users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Please sign in"));
        return carts.findByUser(user).orElseGet(() -> carts.save(new Cart(user)));
    }

    private Product activeProduct(Long id) {
        return products.findById(id).filter(Product::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void checkStock(Product p, int qty) {
        if (qty > p.getStock())
            throw new ApiException(HttpStatus.CONFLICT, "Only " + p.getStock() + " left in stock for " + p.getName());
    }
}

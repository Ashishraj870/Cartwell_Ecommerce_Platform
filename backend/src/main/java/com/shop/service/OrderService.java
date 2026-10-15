package com.shop.service;

import com.shop.dto.Dtos.*;
import com.shop.dto.Mapper;
import com.shop.entity.*;
import com.shop.repo.*;
import com.shop.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orders;
    private final CartRepository carts;
    private final ProductRepository products;
    private final UserRepository users;

    public OrderService(OrderRepository orders, CartRepository carts, ProductRepository products, UserRepository users) {
        this.orders = orders; this.carts = carts; this.products = products; this.users = users;
    }

    /** Cart -> Order in a single transaction: lock stock rows, snapshot prices, empty the cart. */
    public OrderRes checkout(String email, String address) {
        User user = user(email);
        Cart cart = carts.findByUser(user).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Your cart is empty"));
        if (cart.getItems().isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "Your cart is empty");

        Order order = new Order(user, address.trim());
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            Product p = products.findForUpdate(ci.getProduct().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "A product in your cart is no longer available"));
            if (!p.isActive() || p.getStock() < ci.getQuantity())
                throw new ApiException(HttpStatus.CONFLICT, "Not enough stock for " + p.getName());
            p.setStock(p.getStock() - ci.getQuantity());
            order.getItems().add(new OrderItem(order, p, ci.getQuantity(), p.getPrice()));
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setTotal(total);
        cart.getItems().clear();
        return Mapper.order(orders.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderRes> mine(String email) {
        return orders.findByUserOrderByCreatedAtDesc(user(email)).stream().map(Mapper::order).toList();
    }

    @Transactional(readOnly = true)
    public OrderRes get(String email, boolean admin, Long id) {
        Order o = find(id);
        if (!admin && !o.getUser().getEmail().equals(email)) throw new ApiException(HttpStatus.NOT_FOUND, "Order not found");
        return Mapper.order(o);
    }

    public OrderRes cancel(String email, Long id) {
        Order o = find(id);
        if (!o.getUser().getEmail().equals(email)) throw new ApiException(HttpStatus.NOT_FOUND, "Order not found");
        if (o.getStatus() != OrderStatus.PLACED && o.getStatus() != OrderStatus.CONFIRMED)
            throw new ApiException(HttpStatus.BAD_REQUEST, "This order has already shipped and can't be cancelled");
        restockAndCancel(o);
        return Mapper.order(o);
    }

    @Transactional(readOnly = true)
    public List<OrderRes> all() { return orders.findAllByOrderByCreatedAtDesc().stream().map(Mapper::order).toList(); }

    public OrderRes updateStatus(Long id, OrderStatus next) {
        Order o = find(id);
        OrderStatus cur = o.getStatus();
        if (cur == OrderStatus.CANCELLED || cur == OrderStatus.DELIVERED)
            throw new ApiException(HttpStatus.BAD_REQUEST, "A " + cur.name().toLowerCase() + " order can't be changed");
        if (next == OrderStatus.CANCELLED) restockAndCancel(o);
        else if (next.ordinal() <= cur.ordinal()) throw new ApiException(HttpStatus.BAD_REQUEST, "Status can only move forward");
        else o.setStatus(next);
        return Mapper.order(o);
    }

    private void restockAndCancel(Order o) {
        for (OrderItem i : o.getItems()) {
            Product p = products.findForUpdate(i.getProduct().getId()).orElseThrow();
            p.setStock(p.getStock() + i.getQuantity());
        }
        o.setStatus(OrderStatus.CANCELLED);
    }

    private Order find(Long id) { return orders.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found")); }
    private User user(String email) { return users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Please sign in")); }
}

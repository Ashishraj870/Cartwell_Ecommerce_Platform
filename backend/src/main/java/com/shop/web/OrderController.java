package com.shop.web;

import com.shop.dto.Dtos.*;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    // ---- customer ----
    @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED)
    public OrderRes place(Authentication a, @Valid @RequestBody CheckoutReq r) { return service.checkout(a.getName(), r.shippingAddress()); }

    @GetMapping("/orders") public List<OrderRes> mine(Authentication a) { return service.mine(a.getName()); }

    @GetMapping("/orders/{id}")
    public OrderRes get(Authentication a, @PathVariable Long id) {
        boolean admin = a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
        return service.get(a.getName(), admin, id);
    }

    @PostMapping("/orders/{id}/cancel")
    public OrderRes cancel(Authentication a, @PathVariable Long id) { return service.cancel(a.getName(), id); }

    // ---- admin ----
    @GetMapping("/admin/orders") public List<OrderRes> all() { return service.all(); }

    @PutMapping("/admin/orders/{id}/status")
    public OrderRes status(@PathVariable Long id, @Valid @RequestBody StatusReq r) { return service.updateStatus(id, r.status()); }
}

package com.shop.web;

import com.shop.dto.Dtos.*;
import com.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @GetMapping("/products")
    public Page<ProductRes> list(@RequestParam(required = false) String q, @RequestParam(required = false) Long categoryId,
                                 @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        return service.search(q, categoryId, page, size);
    }

    @GetMapping("/products/{id}") public ProductRes get(@PathVariable Long id) { return service.get(id); }

    @PostMapping("/products") @ResponseStatus(HttpStatus.CREATED)
    public ProductRes create(@Valid @RequestBody ProductReq r) { return service.create(r); }

    @PutMapping("/products/{id}")
    public ProductRes update(@PathVariable Long id, @Valid @RequestBody ProductReq r) { return service.update(id, r); }

    @DeleteMapping("/products/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    @GetMapping("/categories") public List<CategoryRes> categories() { return service.categories(); }

    @PostMapping("/categories") @ResponseStatus(HttpStatus.CREATED)
    public CategoryRes addCategory(@Valid @RequestBody CategoryReq r) { return service.addCategory(r.name()); }
}

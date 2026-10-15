package com.shop.service;

import com.shop.dto.Dtos.*;
import com.shop.dto.Mapper;
import com.shop.entity.*;
import com.shop.repo.*;
import com.shop.web.ApiException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductService(ProductRepository products, CategoryRepository categories) {
        this.products = products; this.categories = categories;
    }

    @Transactional(readOnly = true)
    public Page<ProductRes> search(String q, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "id"));
        return products.search(q == null ? "" : q.trim(), categoryId, pageable).map(Mapper::product);
    }

    @Transactional(readOnly = true)
    public ProductRes get(Long id) { return Mapper.product(find(id)); }

    public ProductRes create(ProductReq r) {
        Product p = new Product();
        apply(p, r);
        return Mapper.product(products.save(p));
    }

    public ProductRes update(Long id, ProductReq r) {
        Product p = find(id);
        apply(p, r);
        return Mapper.product(p);
    }

    /** Soft delete so historical orders keep their product reference. */
    public void delete(Long id) { find(id).setActive(false); }

    @Transactional(readOnly = true)
    public List<CategoryRes> categories() {
        return categories.findAll().stream().map(c -> new CategoryRes(c.getId(), c.getName())).toList();
    }

    public CategoryRes addCategory(String name) {
        if (categories.existsByNameIgnoreCase(name.trim()))
            throw new ApiException(HttpStatus.CONFLICT, "That category already exists");
        Category c = categories.save(new Category(name.trim()));
        return new CategoryRes(c.getId(), c.getName());
    }

    private Product find(Long id) {
        return products.findById(id).filter(Product::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void apply(Product p, ProductReq r) {
        p.setName(r.name().trim());
        p.setDescription(r.description());
        p.setPrice(r.price());
        p.setStock(r.stock());
        p.setImageUrl(r.imageUrl());
        p.setCategory(categories.findById(r.categoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Category not found")));
    }
}

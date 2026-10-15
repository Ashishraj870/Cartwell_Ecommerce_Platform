package com.shop.repo;

import com.shop.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
           select p from Product p
           where p.active = true
             and lower(p.name) like lower(concat('%', :q, '%'))
             and (:cat is null or p.category.id = :cat)
           """)
    Page<Product> search(@Param("q") String q, @Param("cat") Long categoryId, Pageable pageable);

    /** Row lock so two simultaneous checkouts can't oversell the last unit. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findForUpdate(@Param("id") Long id);
}

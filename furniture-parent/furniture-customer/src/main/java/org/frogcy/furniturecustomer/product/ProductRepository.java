package org.frogcy.furniturecustomer.product;

import org.frogcy.furniturecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
        SELECT p FROM Product p
        WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.alias) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND p.deleted = false
""")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    List<Product> findTop10ByOrderByCreatedAtDesc();
}

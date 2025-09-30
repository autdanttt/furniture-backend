package org.frogcy.furnitureadmin.product;

import org.frogcy.furniturecommon.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
      SELECT p FROM Product p
      WHERE p.name = :name
      AND p.deleted = false
""")
    Optional<Product> findByName(String name);

    @Query("""
      SELECT p FROM Product p
      WHERE p.alias = :alias
      AND p.deleted = false
""")
    Optional<Product> findByAlias(String alias);
}

package org.frogcy.furnitureadmin.inventory;

import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProduct(Product product);

//    Page<Inventory> search(String keyword, Pageable pageable);
}

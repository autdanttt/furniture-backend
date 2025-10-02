package org.frogcy.furnitureadmin.inventory;

import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    Optional<InventoryTransaction> findByInventoryId(Integer inventoryId);

    InventoryTransaction findTopByInventory_IdOrderByTransactionDateDesc(Integer inventoryId);
}

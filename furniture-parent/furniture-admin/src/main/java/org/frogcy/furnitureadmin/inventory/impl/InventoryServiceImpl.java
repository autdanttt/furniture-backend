package org.frogcy.furnitureadmin.inventory.impl;

import org.frogcy.furnitureadmin.inventory.*;
import org.frogcy.furnitureadmin.inventory.dto.InventoryResponseDTO;
import org.frogcy.furnitureadmin.product.ProductNotFoundException;
import org.frogcy.furnitureadmin.product.ProductRepository;
import org.frogcy.furnitureadmin.product.dto.ProductResponseDTO;
import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.InventoryTransaction;
import org.frogcy.furniturecommon.entity.InventoryTransactionType;
import org.frogcy.furniturecommon.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryTransactionRepository transactionRepository, ProductRepository productRepository, InventoryTransactionRepository inventoryTransactionRepository) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }
     private void increaseStock(Product product, Integer quantity, String note) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProduct(product);
                    inv.setQuantity(0);
                    return inventoryRepository.save(inv);
                });

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setLastUpdated(new Date());
        inventoryRepository.save(inventory);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(inventory);
        tx.setQuantityChanged(quantity);
        tx.setType(InventoryTransactionType.IMPORT);
        tx.setNote(note);
        transactionRepository.save(tx);
    }

     private void decreaseStock(Product product, Integer quantity, String note) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new InventoryNotFoundException("No inventory found for product"));

        if (inventory.getQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock for product " + product.getName());
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setLastUpdated(new Date());
        inventoryRepository.save(inventory);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(inventory);
        tx.setQuantityChanged(-quantity);
        tx.setType(InventoryTransactionType.SALE);
        tx.setNote(note);
        transactionRepository.save(tx);
    }

    @Override
    public boolean inStock(Product product) {
        return inventoryRepository.findByProduct(product)
                .map(inv -> inv.getQuantity() > 0)
                .orElse(false);
    }

    @Override
    public InventoryResponseDTO importProduct(Integer productId, InventoryRequestDTO dto) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("No product found for id: " + productId)
        );
        increaseStock(product, dto.getQuantity(), dto.getNote());

        product = productRepository.findById(productId).get();
        Inventory inventory = inventoryRepository.findByProduct(product).get();
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findByInventoryId(inventory.getId()).orElseThrow(
                () -> new InventoryTransactionNotFoundException("No inventory transaction found for id: " + inventory.getId())
        );

        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    @Override
    public InventoryResponseDTO exportProduct(Integer productId, InventoryRequestDTO dto) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("No product found for id: " + productId)
        );
        decreaseStock(product, dto.getQuantity(), dto.getNote());

        product = productRepository.findById(productId).get();
        Inventory inventory = inventoryRepository.findByProduct(product).get();
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findByInventoryId(inventory.getId()).orElseThrow(
                () -> new InventoryTransactionNotFoundException("No inventory transaction found for id: " + inventory.getId())
        );

        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    @Override
    public InventoryResponseDTO update(Integer productId, InventoryUpdateDTO dto) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("No product found for id: " + productId)
        );

        updateProductQuantity(dto, product);

        product = productRepository.findById(productId).get();
        Inventory inventory = inventoryRepository.findByProduct(product).get();
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findByInventoryId(inventory.getId()).orElseThrow(
                () -> new InventoryTransactionNotFoundException("No inventory transaction found for id: " + inventory.getId())
        );

        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    @Override
    public InventoryResponseDTO get(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("No product found for id: " + productId)
        );

        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new InventoryNotFoundException("No inventory found for product id: " + productId));

        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findByInventoryId(inventory.getId()).orElseThrow(
                () -> new InventoryTransactionNotFoundException("No inventory transaction found for id: " + inventory.getId())
        );

        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    private void updateProductQuantity(InventoryUpdateDTO dto, Product product) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProduct(product);
                    inv.setQuantity(0);
                    return inventoryRepository.save(inv);
                });

        inventory.setQuantity(dto.getQuantity());
        inventory.setLastUpdated(new Date());
        inventoryRepository.save(inventory);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(inventory);
        tx.setQuantityChanged(Math.abs(dto.getQuantity() - inventory.getQuantity()));
        tx.setType(InventoryTransactionType.IMPORT);
        tx.setNote("");
        transactionRepository.save(tx);
    }

    private static InventoryResponseDTO getInventoryResponseDTO(Inventory inventory, Product product, InventoryTransaction inventoryTransaction) {
        InventoryResponseDTO response = new InventoryResponseDTO();
        response.setId(inventory.getId());
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setType(InventoryTransactionType.IMPORT);
        response.setQuantity(inventory.getQuantity());
        response.setQuantityChanged(inventoryTransaction.getQuantityChanged());
        response.setTransactionDate(inventoryTransaction.getTransactionDate());
        response.setNotes(inventoryTransaction.getNote());
        response.setLastUpdated(inventory.getLastUpdated());
        return response;
    }
}

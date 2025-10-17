package org.frogcy.furnitureadmin.inventory.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.frogcy.furnitureadmin.inventory.*;
import org.frogcy.furnitureadmin.inventory.dto.InventoryResponseDTO;
import org.frogcy.furnitureadmin.inventory.dto.InventoryTransactionDTO;
import org.frogcy.furnitureadmin.product.ProductNotFoundException;
import org.frogcy.furnitureadmin.product.ProductRepository;
import org.frogcy.furnitureadmin.product.dto.ProductInventoryDTO;
import org.frogcy.furnitureadmin.product.dto.ProductResponseDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.Category;
import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.InventoryTransaction;
import org.frogcy.furniturecommon.entity.InventoryTransactionType;
import org.frogcy.furniturecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findTopByInventory_IdOrderByTransactionDateDesc(inventory.getId());

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
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findTopByInventory_IdOrderByTransactionDateDesc(inventory.getId());

        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    @Override
    public InventoryResponseDTO update(Integer productId, InventoryRequestDTO dto) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("No product found for id: " + productId)
        );

        updateProductQuantity(dto, product);

        product = productRepository.findById(productId).get();
        Inventory inventory = inventoryRepository.findByProduct(product).get();
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findTopByInventory_IdOrderByTransactionDateDesc(inventory.getId());
        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    @Override
    public InventoryResponseDTO get(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("No product found for id: " + productId)
        );

        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new InventoryNotFoundException("No inventory found for product id: " + productId));

        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findTopByInventory_IdOrderByTransactionDateDesc(inventory.getId());

        return getInventoryResponseDTO(inventory, product, inventoryTransaction);
    }

    @Override
    public PageResponseDTO<ProductInventoryDTO> getAllInventory(
            int page,
            int size,
            String sortField,
            String sortDir,
            String keyword
    ) {
        // Thiết lập sort direction
        Sort sort = Sort.unsorted();
        if (!"quantity".equalsIgnoreCase(sortField)) {
            sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortField).descending()
                    : Sort.by(sortField).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = (root, query, cb) -> {
            // Dùng fetch join để tránh N+1 query
            root.fetch("inventory", JoinType.LEFT);
            root.fetch("category", JoinType.LEFT);
            query.distinct(true);

            Join<Product, Inventory> inventoryJoin = root.join("inventory", JoinType.LEFT);
            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm theo tên sản phẩm
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }

            // Chỉ lấy sản phẩm chưa bị xóa mềm
            predicates.add(cb.isFalse(root.get("deleted")));

            // Xử lý sort linh hoạt (nếu sortField là quantity thì sort theo inventory.quantity)
            if ("quantity".equalsIgnoreCase(sortField)) {
                query.orderBy(sortDir.equalsIgnoreCase("desc")
                        ? cb.desc(inventoryJoin.get("quantity"))
                        : cb.asc(inventoryJoin.get("quantity")));
            } else {
                query.orderBy(sortDir.equalsIgnoreCase("desc")
                        ? cb.desc(root.get(sortField))
                        : cb.asc(root.get(sortField)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Truy vấn
        Page<Product> pageResult = productRepository.findAll(spec, pageable);

        // Map sang DTO
        List<ProductInventoryDTO> dtos = pageResult.getContent().stream()
                .map(p -> new ProductInventoryDTO(
                        p.getId(),
                        p.getName(),
                        p.getMainImage().getImageUrl(),
                        p.getCategory() != null ? p.getCategory().getName() : null,
                        p.getInventory() != null ? p.getInventory().getQuantity() : 0,
                        p.getInventory() != null ? p.getInventory().getLastUpdated() : null
                ))
                .toList();

        // Trả về kết quả phân trang
        return new PageResponseDTO<>(
                dtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    public List<InventoryTransactionDTO> getListInventoryTransaction(Integer inventoryId) {
        List<InventoryTransaction> inventories = inventoryTransactionRepository.findByInventoryId(inventoryId);

        return inventories.stream().map(
               inventoryTransaction -> {
                   InventoryTransactionDTO dto = new InventoryTransactionDTO();
                   dto.setId(inventoryTransaction.getId());
                   dto.setInventoryId(inventoryTransaction.getInventory().getId());
                   dto.setQuantityChanged(inventoryTransaction.getQuantityChanged());
                   dto.setTransactionDate(inventoryTransaction.getTransactionDate());
                   dto.setType(inventoryTransaction.getType());
                   dto.setNote(inventoryTransaction.getNote());
                   return dto;
               }
        ).toList();
    }

//    @Override
//    public PageResponseDTO<ProductInventoryDTO> getAllInventory(int page, int size, String sortField, String sortDir, String keyword) {
//        Sort sort = sortDir.equalsIgnoreCase("desc")
//                ? Sort.by(sortField).descending()
//                : Sort.by(sortField).ascending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Specification<Product> spec = (root, query, cb) -> {
//            // Join Inventory
//            Join<Product, Inventory> inventoryJoin = root.join("inventory", JoinType.LEFT);
//            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
//
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (keyword != null && !keyword.isEmpty()) {
//                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
//            }
//
//            predicates.add(cb.isFalse(root.get("deleted")));
//
//            // Nếu sort theo quantity
//            if ("quantity".equalsIgnoreCase(sortField)) {
//                query.orderBy(sortDir.equalsIgnoreCase("desc")
//                        ? cb.desc(inventoryJoin.get("quantity"))
//                        : cb.asc(inventoryJoin.get("quantity")));
//            }
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//
//        Page<Product> pageResult = productRepository.findAll(spec, pageable);
//
//
//        List<ProductInventoryDTO> dtos = pageResult.getContent().stream()
//                .map(p -> new ProductInventoryDTO(
//                        p.getId(),
//                        p.getName(),
//                        p.getCategory() != null ? p.getCategory().getName() : null,
//                        p.getInventory() != null ? p.getInventory().getQuantity() : 0
//                ))
//                .toList();
//
//        return new PageResponseDTO<>(
//                dtos,
//                pageResult.getNumber(),
//                pageResult.getSize(),
//                pageResult.getTotalElements(),
//                pageResult.getTotalPages()
//        );
//    }

    private void updateProductQuantity(InventoryRequestDTO dto, Product product) {
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProduct(product);
                    inv.setQuantity(0);
                    return inventoryRepository.save(inv);
                });
        int oldQuantity = inventory.getQuantity();
        int newQuantity = dto.getQuantity();

        inventory.setQuantity(dto.getQuantity());
        inventory.setLastUpdated(new Date());

        inventoryRepository.save(inventory);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(inventory);
        tx.setQuantityChanged(newQuantity - oldQuantity);
        tx.setType(InventoryTransactionType.ADJUST);
        tx.setNote(dto.getNote());
        transactionRepository.save(tx);
    }

    private static InventoryResponseDTO getInventoryResponseDTO(Inventory inventory, Product product, InventoryTransaction inventoryTransaction) {
        InventoryResponseDTO response = new InventoryResponseDTO();
        response.setId(inventory.getId());
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setType(inventoryTransaction.getType());
        response.setQuantity(inventory.getQuantity());
        response.setQuantityChanged(inventoryTransaction.getQuantityChanged());
        response.setTransactionDate(inventoryTransaction.getTransactionDate());
        response.setNotes(inventoryTransaction.getNote());
        response.setLastUpdated(inventory.getLastUpdated());
        return response;
    }
}

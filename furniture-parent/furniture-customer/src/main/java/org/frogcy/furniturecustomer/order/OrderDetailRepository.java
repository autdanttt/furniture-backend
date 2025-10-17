package org.frogcy.furniturecustomer.order;

import org.frogcy.furniturecommon.entity.order.OrderDetail;
import org.frogcy.furniturecommon.entity.product.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query("""
        SELECT od.product
        FROM OrderDetail od
        JOIN od.order o
        WHERE o.orderTime BETWEEN :startDate AND :endDate
        GROUP BY od.product
        ORDER BY SUM(od.quantity) DESC
    """)
    List<Product> findTopSellingProducts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );
}

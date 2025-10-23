package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.dashboard.dto.BestSellingProductProjection;
import org.frogcy.furniturecommon.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query(value = "SELECT " +
            "p.id as productId, " +
            "p.name as productName, " +
            "pi.image_url as mainImageUrl, " + // Thay đổi ở đây
            "SUM(od.quantity) as totalQuantity " +
            "FROM order_details od " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN orders o ON od.order_id = o.id " +
            "LEFT JOIN product_images pi ON p.main_image_id = pi.id " + // Thay đổi ở đây
            "WHERE o.order_time >= :startDate AND o.order_time < :endDate " +
            "AND o.status <> 'CANCELLED' AND o.payment_status = 'PAID' " +
            "GROUP BY p.id, p.name, pi.image_url " +
            "ORDER BY totalQuantity DESC " +
            "LIMIT 10", nativeQuery = true) // Thêm LIMIT và chuyển thành nativeQuery
    List<BestSellingProductProjection> findBestSellingProducts(@Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate);
}

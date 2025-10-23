package org.frogcy.furnitureadmin.order;

import org.frogcy.furnitureadmin.dashboard.dto.StatsDataPoint;
import org.frogcy.furnitureadmin.dashboard.dto.StatsProjection;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, OrderRepositoryCustom {
    @Query("""
        SELECT o FROM Order o
        WHERE (LOWER(o.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(o.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
        OR LOWER(o.addressLine) LIKE LOWER(CONCAT(:keyword, '%'))
        OR LOWER(o.phoneNumber) LIKE LOWER(CONCAT(:keyword, '%'))
        OR LOWER(o.provinceName) LIKE LOWER(CONCAT(:keyword, '%'))
        OR LOWER(o.wardName) LIKE LOWER(CONCAT(:keyword, '%'))
         
""")
    Page<Order> search(String keyword, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = :status")
    long sumTotalByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = :orderStatus")
    long countByStatus(OrderStatus orderStatus);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.orderTime, '%Y-%m-%d') as label, SUM(o.total) as revenue, COUNT(o.id) as orderCount " +
            "FROM Order o " +
            "WHERE o.orderTime >= :startDate AND o.orderTime < :endDate " +
            "AND o.status <> 'CANCELLED' AND o.paymentStatus = 'PAID' " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.orderTime, '%Y-%m-%d') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.orderTime, '%Y-%m-%d') ASC")
    List<StatsProjection> findStatsGroupedByDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.orderTime, '%Y-%m') as label, SUM(o.total) as revenue, COUNT(o.id) as orderCount " +
            "FROM Order o " +
            "WHERE o.orderTime >= :startDate AND o.orderTime < :endDate " +
            "AND o.status <> 'CANCELLED' AND o.paymentStatus = 'PAID' " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.orderTime, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.orderTime, '%Y-%m') ASC")
    List<StatsProjection> findStatsGroupedByMonth(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.orderTime, '%H') as label, SUM(o.total) as revenue, COUNT(o.id) as orderCount " +
            "FROM Order o " +
            "WHERE o.orderTime >= :startDate AND o.orderTime < :endDate " +
            "AND o.status <> 'CANCELLED' AND o.paymentStatus = 'PAID' " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.orderTime, '%H') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.orderTime, '%H') ASC")
    List<StatsProjection> findStatsGroupedByHour(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
//    /    @Query("""
//    SELECT COALESCE(SUM(o.total), 0)
//    FROM Order o
//    WHERE o.status IN (
//        org.frogcy.furniturecommon.entity.order.OrderStatus.DELIVERED,
//    )
//""")
//    long sumValidTotal();
}

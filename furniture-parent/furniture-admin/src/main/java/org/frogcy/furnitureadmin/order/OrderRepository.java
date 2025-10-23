package org.frogcy.furnitureadmin.order;

import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

//    @Query("""
//    SELECT COALESCE(SUM(o.total), 0)
//    FROM Order o
//    WHERE o.status IN (
//        org.frogcy.furniturecommon.entity.order.OrderStatus.DELIVERED,
//    )
//""")
//    long sumValidTotal();
}

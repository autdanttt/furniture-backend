package org.frogcy.furnitureadmin.order;

import org.frogcy.furniturecommon.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
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
}

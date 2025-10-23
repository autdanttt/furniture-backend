package org.frogcy.furnitureadmin.customer;

import org.frogcy.furniturecommon.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("""
        SELECT c FROM Customer c
        WHERE (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
        OR LOWER(c.phoneNumber) LIKE LOWER(CONCAT(:keyword, '%'))
""")
    Page<Customer> search(String keyword, Pageable pageable);

    long countByDeletedFalse();

    List<Customer> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}

package org.frogcy.furniturecustomer.customer;

import org.frogcy.furniturecommon.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    @Query("""
        SELECT u FROM Customer u
        WHERE u.email = :email
        AND u.deleted = false
""")
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.email= ?1 AND c.deleted = false")
    public Customer getUserByEmail(String email);

    Optional<Customer> findCustomerByEmailAndVerifiedIsFalse(String email);
}

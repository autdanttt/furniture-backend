package org.frogcy.furniturecustomer.auth;

import org.frogcy.furniturecommon.entity.CustomerRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<CustomerRefreshToken, Integer> {
    @Query("SELECT rt FROM CustomerRefreshToken rt WHERE rt.customer.email = ?1 AND rt.customer.deleted = false ")
    public List<CustomerRefreshToken> findByEmail(String email);

    @Query("DELETE FROM CustomerRefreshToken rt WHERE rt.expiryTime <= CURRENT_TIME")
    @Modifying
    public int deleteByExpiryTime();
}

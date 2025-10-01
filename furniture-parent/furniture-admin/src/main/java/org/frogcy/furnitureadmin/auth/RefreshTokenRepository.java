package org.frogcy.furnitureadmin.auth;

import org.frogcy.furniturecommon.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<UserRefreshToken, Integer> {

    @Query("SELECT rt FROM UserRefreshToken rt WHERE rt.user.email = ?1 AND rt.user.deleted = false ")
    public List<UserRefreshToken> findByEmail(String email);

    @Query("DELETE FROM UserRefreshToken rt WHERE rt.expiryTime <= CURRENT_TIME")
    @Modifying
    public int deleteByExpiryTime();

}

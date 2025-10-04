package org.frogcy.furniturecustomer.otp;

import org.frogcy.furniturecommon.entity.Otp;
import org.frogcy.furniturecommon.entity.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findTopByCustomerIdAndTypeAndUsedIsFalseOrderByCreatedAtDesc(Integer customerId, OtpType type);

}

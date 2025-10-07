package org.frogcy.furniturecustomer.shippingfee;

import org.frogcy.furniturecommon.entity.ShippingFee;
import org.frogcy.furniturecommon.entity.address.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
    @Query("""
       SELECT sf FROM ShippingFee sf
       WHERE sf.province.code = :provinceCode
""")
    Optional<ShippingFee> findByProvinceCode(Integer provinceCode);
}

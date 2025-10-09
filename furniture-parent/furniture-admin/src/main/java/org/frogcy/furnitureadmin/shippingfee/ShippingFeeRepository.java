package org.frogcy.furnitureadmin.shippingfee;

import org.frogcy.furniturecommon.entity.ShippingFee;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
    @Query("""
        SELECT sf FROM ShippingFee sf
        WHERE LOWER(sf.province.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        AND sf.deleted = false
""")
    Page<ShippingFee> search(String keyword, Pageable pageable);

    Optional<ShippingFee> findByProvinceCodeAndDeletedIsFalse(Integer provinceCode);
}

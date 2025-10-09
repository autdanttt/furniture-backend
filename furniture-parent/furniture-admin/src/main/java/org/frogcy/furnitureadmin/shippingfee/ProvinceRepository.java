package org.frogcy.furnitureadmin.shippingfee;

import org.frogcy.furniturecommon.entity.address.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    Optional<Province> findByCode(Integer provinceCode);
}

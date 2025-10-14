package org.frogcy.furnitureadmin.seeder;

import org.frogcy.furniturecommon.entity.address.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardRepository extends JpaRepository<Ward, Integer> {
}

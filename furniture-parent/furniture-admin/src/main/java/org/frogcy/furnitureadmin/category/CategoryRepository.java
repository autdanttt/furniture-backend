package org.frogcy.furnitureadmin.category;

import jakarta.validation.constraints.NotNull;
import org.frogcy.furniturecommon.entity.Category;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("""
        SELECT c FROM Category c
        WHERE c.name = :name
        AND c.deleted = false
""")
    Optional<Category> findByName(String name);


    @Query("""
        SELECT c FROM Category c
        WHERE c.alias = :name
        AND c.deleted = false
""")
    Optional<Category> findByAlias(String alias);
}

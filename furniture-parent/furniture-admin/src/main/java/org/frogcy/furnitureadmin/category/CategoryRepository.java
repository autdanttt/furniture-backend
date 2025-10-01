package org.frogcy.furnitureadmin.category;

import org.frogcy.furniturecommon.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
        WHERE c.alias = :alias
        AND c.deleted = false
""")
    Optional<Category> findByAlias(String alias);

    @Query("""
        SELECT c FROM Category c
        WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.alias) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND c.deleted = false
""")
    Page<Category> search(@Param("keyword") String keyword, Pageable pageable);
}
